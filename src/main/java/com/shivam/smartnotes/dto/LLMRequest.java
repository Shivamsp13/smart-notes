package com.shivam.smartnotes.dto;

import lombok.Data;

@Data
public class LLMRequest {

    private String userPrompt;
    private String systemPrompt;

    public LLMRequest() {}

    public LLMRequest(String userPrompt, String systemPrompt) {
        this.userPrompt = userPrompt;
        this.systemPrompt = systemPrompt;
    }

}
