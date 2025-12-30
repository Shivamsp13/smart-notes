package com.shivam.smartnotes.utils;

import java.util.*;
import java.util.regex.Pattern;

public final class ChunkingUtil {

    private ChunkingUtil() {}

    private static final int MAX_CHUNK_LENGTH = 500;

    private static final Pattern SENTENCE_SPLITTER =
            Pattern.compile("(?<=[.!?])\\s+");

    private static final Set<String> STOP_WORDS = Set.of(
            "the","is","are","and","or","of","to","in",
            "a","an","on","for","with","as","by","at"
    );

    public static List<ChunkData> chunkText(String rawText) {

        if (rawText == null || rawText.isBlank()) {
            return Collections.emptyList();
        }

        List<String> sentences = splitIntoSentences(rawText);
        List<ChunkData> chunks = new ArrayList<>();

        StringBuilder currentChunk = new StringBuilder();
        int orderIndex = 0;

        for (String sentence : sentences) {

            if (currentChunk.length() + sentence.length() > MAX_CHUNK_LENGTH) {
                chunks.add(buildChunk(currentChunk.toString(), orderIndex++));
                currentChunk.setLength(0);
            }

            currentChunk.append(sentence).append(" ");
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(buildChunk(currentChunk.toString(), orderIndex));
        }

        return chunks;
    }

    private static List<String> splitIntoSentences(String text) {
        return Arrays.asList(SENTENCE_SPLITTER.split(text.trim()));
    }

    private static ChunkData buildChunk(String text, int orderIndex) {
        Set<String> keywords = extractKeywords(text);
        return new ChunkData(text.trim(), orderIndex, keywords);
    }

    private static Set<String> extractKeywords(String text) {

        Set<String> keywords = new HashSet<>();
        String[] words = text.toLowerCase().split("\\W+");

        for (String word : words) {
            if (word.length() > 3 && !STOP_WORDS.contains(word)) {
                keywords.add(word);
            }
        }
        return keywords;
    }
}
