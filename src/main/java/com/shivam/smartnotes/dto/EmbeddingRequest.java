package com.shivam.smartnotes.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmbeddingRequest {
    private List<String> texts;

    public EmbeddingRequest(List<String> texts){
        this.texts=texts;
    }
}
