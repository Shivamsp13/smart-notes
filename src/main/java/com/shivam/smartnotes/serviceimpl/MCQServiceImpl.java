package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.dto.MCQItem;
import com.shivam.smartnotes.dto.MCQResponse;
import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.MCQ;
import com.shivam.smartnotes.repository.ChunkRepository;
import com.shivam.smartnotes.repository.MCQRepository;
import com.shivam.smartnotes.service.EmbeddingService;
import com.shivam.smartnotes.service.LLMService;
import com.shivam.smartnotes.service.MCQService;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MCQServiceImpl implements MCQService {

    private static final int TOP_K_CHUNKS=5;
    private final ChunkRepository chunkRepository;
    private final MCQRepository mcqRepository;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MCQServiceImpl(ChunkRepository chunkRepository,
                          MCQRepository mcqRepository,
                          EmbeddingService embeddingService,
                          LLMService llmService) {
        this.chunkRepository = chunkRepository;
        this.mcqRepository = mcqRepository;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }

    @Override
    public List<MCQ> generateMcqs(Long userId, String topic, int count) {

        String topicEmbedding =
                embeddingService.generateEmbedding(topic);

        List<Chunk> userChunks =
                chunkRepository.findByNotes_User_UserId(userId);

        if (userChunks.isEmpty()) {
            throw new RuntimeException("No notes found for user");
        }

        List<Chunk> topChunks =
                userChunks.stream()
                        .sorted(Comparator.comparingDouble(
                                (Chunk chunk) -> embeddingService.cosineSimilarity(
                                        chunk.getEmbedding(),
                                        topicEmbedding
                                )
                        ).reversed())
                        .limit(TOP_K_CHUNKS)
                        .collect(Collectors.toList());


        if(topChunks.isEmpty()){
            throw  new RuntimeException("No relevant chunks found for this topic"+topic);
        }

        String context =
                topChunks.stream()
                        .map(Chunk::getChunkText)
                        .collect(Collectors.joining("\n\n"));


        //here two things needs to be done 1)SystemPrompt and 2)UserPrompt
        //They will be set to the LLM using service layer of LLM

        String systemPrompt=
                "You are an expert educational content generator."+
                        "Generate high quality MCQs strictly from provided content.";

        String userPrompt=
                """
                Context:
                "%s"
                
                Generate %d MCQs on topic:"%s".
                
                Rules:
                1)Exactly 4 options for each question.
                2)One correct answer only.
                3)Output only valid JSON.
                
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
                
                """.formatted(context,count,topic,topic);

        String rawResponse=llmService.generate(systemPrompt,userPrompt);

        MCQResponse response;
        try{
            response=objectMapper.readValue(rawResponse,MCQResponse.class);
        }
        catch (Exception e){
            throw new RuntimeException("Parsing of LLM Response failed");
        }

        validateResponse(response,count);
        List<MCQItem> items = response.getMcqs();

        List<MCQ> mcqs =
                IntStream.range(0, items.size())
                        .mapToObj(i -> {
                            Chunk chunk = topChunks.get(i % topChunks.size());
                            return toEntity(items.get(i), topic, chunk);
                        })
                        .collect(Collectors.toList());

        mcqRepository.saveAll(mcqs);
        return mcqs;
    }

    @Override
    public List<MCQ> getMcqsByTopic(Long userId, String topic) {

        List<MCQ> mcqs =
                mcqRepository.findByTopicAndNotes_User_UserId(
                        topic,
                        userId
                );

        if (mcqs.isEmpty()) {
            throw new RuntimeException(
                    "No MCQs found for topic: " + topic
            );
        }
        return mcqs;
    }

    private void validateResponse(MCQResponse response, int count) {
        if (response.getMcqs() == null ||
            response.getMcqs().size() != count) {
            throw new RuntimeException("Invalid MCQ count from LLM");
        }

        for (MCQItem m : response.getMcqs()) {
            if (m.getOptions().size() != 4 ||
                m.getCorrectOptionIndex() < 0 ||
                m.getCorrectOptionIndex() > 3) {
                throw new RuntimeException("Invalid MCQ structure");
            }
        }
    }
    private MCQ toEntity(
            MCQItem dto,
            String topic,
            Chunk sourceChunk
    ) {
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
}
