package com.shivam.smartnotes.dto;

import lombok.Data;

import java.util.List;

@Data
public class MCQSubmitResponse {
    private int score;
    private int total;
    private List<MCQResultItem> results;
}
