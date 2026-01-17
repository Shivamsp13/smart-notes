package com.shivam.smartnotes.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.smartnotes.dto.MCQItem;
import com.shivam.smartnotes.dto.MCQResponse;
import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.MCQ;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.InternalSeviceException;
import com.shivam.smartnotes.exceptions.ResourceNotFoundException;
import com.shivam.smartnotes.repository.ChunkRepository;
import com.shivam.smartnotes.repository.MCQRepository;
import com.shivam.smartnotes.repository.UserRepository;
import com.shivam.smartnotes.service.EmbeddingService;
import com.shivam.smartnotes.service.LLMService;
import com.shivam.smartnotes.service.MCQService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MCQServiceImpl implements MCQService {

    private static final int TOP_K_CHUNKS = 5;

    private final ChunkRepository chunkRepository;
    private final MCQRepository mcqRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MCQServiceImpl(
            ChunkRepository chunkRepository,
            MCQRepository mcqRepository,
            UserRepository userRepository,
            EmbeddingService embeddingService,
            LLMService llmService
    ) {
        this.chunkRepository = chunkRepository;
        this.mcqRepository = mcqRepository;
        this.userRepository = userRepository;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }

    @Override
    public MCQResponse generateMcqs(
            Long noteId,
            String username,
            String topic,
            int count
    ) {

        User user = getUserByUsername(username);

        String topicEmbedding =
                embeddingService.generateEmbedding(topic);

        List<Chunk> userChunks =
                chunkRepository.findByNote_NoteIdAndNote_Owner(
                        noteId,
                        user
                );

        if (userChunks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No content found for selected note"
            );
        }

        List<Chunk> topChunks =
                userChunks.stream()
                        .sorted(Comparator.comparingDouble(
                                (Chunk c) -> embeddingService.cosineSimilarity(
                                        c.getEmbedding(),
                                        topicEmbedding
                                )
                        ).reversed())
                        .limit(TOP_K_CHUNKS)
                        .toList();

        if (topChunks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No relevant chunks found for topic: " + topic
            );
        }

        String context =
                topChunks.stream()
                        .map(Chunk::getChunkText)
                        .collect(Collectors.joining("\n\n"));

        String systemPrompt =
                "You are an expert educational content generator. " +
                        "Generate high quality MCQs strictly from provided content.";

        String userPrompt =
                """
                Context:
                "%s"

                Generate %d MCQs on topic: "%s".

                Rules:
                1) Exactly 4 options.
                2) One correct answer.
                3) Output ONLY valid JSON.

                JSON format:
                {
                  "topic": "%s",
                  "mcqs": [
                    {
                      "question": "...",
                      "options": ["A","B","C","D"],
                      "correctOptionIndex": 0,
                      "explanation": "..."
                    }
                  ]
                }
                """.formatted(context, count, topic, topic);

        String rawResponse =
                llmService.generate(systemPrompt, userPrompt);

        MCQResponse parsedResponse;
        try {
            parsedResponse =
                    objectMapper.readValue(rawResponse, MCQResponse.class);
        } catch (Exception e) {
            throw new InternalSeviceException("Failed to parse LLM response");
        }

        validateResponse(parsedResponse, count);

        List<MCQItem> items = parsedResponse.getMcqs();

        List<MCQ> mcqEntities =
                IntStream.range(0, items.size())
                        .mapToObj(i -> {
                            Chunk chunk =
                                    topChunks.get(i % topChunks.size());
                            return toEntity(items.get(i), topic, chunk);
                        })
                        .toList();

        mcqRepository.saveAll(mcqEntities);

        MCQResponse finalResponse = new MCQResponse();
        finalResponse.setTopic(topic);
        finalResponse.setMcqs(items);

        return finalResponse;
    }

    @Override
    public MCQResponse getMcqsByTopic(String username, String topic) {

        User user = getUserByUsername(username);

        List<MCQ> mcqs =
                mcqRepository.findByTopicAndNote_Owner(topic, user);

        if (mcqs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No MCQs found for topic: " + topic
            );
        }

        List<MCQItem> items =
                mcqs.stream()
                        .map(mcq -> {
                            MCQItem item = new MCQItem();
                            item.setQuestion(mcq.getQuestion());
                            item.setOptions(mcq.getOptions());
                            item.setCorrectOptionIndex(
                                    mcq.getCorrectOptionIndex()
                            );
                            item.setExplanation(mcq.getExplanation());
                            return item;
                        })
                        .toList();

        MCQResponse response = new MCQResponse();
        response.setTopic(topic);
        response.setMcqs(items);

        return response;
    }

    private void validateResponse(MCQResponse response, int count) {
        if (response.getMcqs() == null ||
                response.getMcqs().size() != count) {
            throw new InternalSeviceException("Invalid MCQ count from LLM");
        }

        for (MCQItem m : response.getMcqs()) {
            if (m.getOptions().size() != 4 ||
                    m.getCorrectOptionIndex() < 0 ||
                    m.getCorrectOptionIndex() > 3) {
                throw new InternalSeviceException("Invalid MCQ structure");
            }
        }
    }

    private MCQ toEntity(MCQItem dto,
                         String topic,
                         Chunk sourceChunk) {

        return new MCQ(
                topic,
                dto.getQuestion(),
                dto.getOptions(),
                dto.getCorrectOptionIndex(),
                dto.getExplanation(),
                sourceChunk.getNote(),
                sourceChunk
        );
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + username
                        )
                );
    }
}
