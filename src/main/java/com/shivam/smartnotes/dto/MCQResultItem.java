package com.shivam.smartnotes.dto;

import lombok.Data;

import java.util.List;

@Data
public class MCQResultItem {

    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private int selectedOptionIndex;
    private String explanation;
    private boolean correct;
}