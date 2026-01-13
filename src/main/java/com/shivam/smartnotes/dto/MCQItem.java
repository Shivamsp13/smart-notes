package com.shivam.smartnotes.dto;

import lombok.Data;

import java.util.List;

@Data
public class MCQItem {
    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;

}
