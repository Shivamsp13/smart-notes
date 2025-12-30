package com.shivam.smartnotes.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponse {
    List<List<Float>> embeddings;
}
