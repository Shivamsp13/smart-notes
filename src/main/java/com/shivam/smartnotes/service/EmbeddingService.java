package com.shivam.smartnotes.service;


public interface EmbeddingService {
    String generateEmbedding(String text);
    double cosineSimilarity(String emb1,String emb2);

}
