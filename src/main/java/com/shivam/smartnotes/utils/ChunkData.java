package com.shivam.smartnotes.utils;

import java.util.Set;

public class ChunkData {

    private final String chunkText;
    private final int orderIndex;
    private final Set<String> keywords;

    public ChunkData(String chunkText, int orderIndex, Set<String> keywords) {
        this.chunkText = chunkText;
        this.orderIndex = orderIndex;
        this.keywords = keywords;
    }

    public String getChunkText() {
        return chunkText;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public Set<String> getKeywords() {
        return keywords;
    }
}
