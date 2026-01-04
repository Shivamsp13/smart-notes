package com.shivam.smartnotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LLMRequest {

    private String model;
    private List<Message> messages;
    private double temperature;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
