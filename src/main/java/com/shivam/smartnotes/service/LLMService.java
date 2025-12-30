package com.shivam.smartnotes.service;


public interface LLMService {
    String generate(String systemPrompt,String userPrompt);
}
