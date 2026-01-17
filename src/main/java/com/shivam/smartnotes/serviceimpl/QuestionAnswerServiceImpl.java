package com.shivam.smartnotes.serviceimpl;

import com.shivam.smartnotes.entity.Chunk;
import com.shivam.smartnotes.entity.User;
import com.shivam.smartnotes.exceptions.ResourceNotFoundException;
import com.shivam.smartnotes.repository.ChunkRepository;
import com.shivam.smartnotes.repository.UserRepository;
import com.shivam.smartnotes.service.EmbeddingService;
import com.shivam.smartnotes.service.LLMService;
import com.shivam.smartnotes.service.QuestionAnswerService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private static final int TOP_K_CHUNKS = 5;
    private static final double COSINE_SIMILARITY_THRESHOLD = 0.35;

    private static final String FALLBACK_RESPONSE =
            "The answer to this question does not exist in your notes.";

    private final ChunkRepository chunkRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;

    public QuestionAnswerServiceImpl(
            ChunkRepository chunkRepository,
            UserRepository userRepository,
            EmbeddingService embeddingService,
            LLMService llmService
    ) {
        this.chunkRepository = chunkRepository;
        this.userRepository = userRepository;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }

    @Override
    public String askQuestion(
            Long noteId,
            String username,
            String question
    ) {

        User user = getUserByUsername(username);

        String questionEmbedding =
                embeddingService.generateEmbedding(question);

        List<Chunk> chunks =
                chunkRepository.findByNote_NoteIdAndNote_Owner(
                        noteId,
                        user
                );

        if (chunks.isEmpty()) {
            return FALLBACK_RESPONSE;
        }

        List<ScoredChunk> ranked =
                chunks.stream()
                        .map(chunk -> new ScoredChunk(
                                chunk,
                                embeddingService.cosineSimilarity(
                                        chunk.getEmbedding(),
                                        questionEmbedding
                                )
                        ))
                        .sorted(Comparator
                                .comparingDouble(ScoredChunk::score)
                                .reversed())
                        .toList();

        if (ranked.get(0).score() < COSINE_SIMILARITY_THRESHOLD) {
            return FALLBACK_RESPONSE;
        }

        String context =
                ranked.stream()
                        .limit(TOP_K_CHUNKS)
                        .map(sc -> sc.chunk().getChunkText())
                        .collect(Collectors.joining("\n\n"));

        String systemPrompt =
                """
                You are a precise assistant.
                Answer ONLY using the provided context.
                If the answer is not clearly present,
                respond exactly with:
                "The answer to this question does not exist in your notes."
                """;

        String userPrompt =
                """
                Context:
                "%s"

                Question:
                "%s"
                """.formatted(context, question);

        return llmService.generate(systemPrompt, userPrompt);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + username
                        )
                );
    }

    private record ScoredChunk(Chunk chunk, double score) {}
}
