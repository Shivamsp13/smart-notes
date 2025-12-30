package com.shivam.smartnotes.dto;

import lombok.Data;
import java.util.List;

@Data
public class MCQResponse {
    private String topic;
    private List<MCQItem> mcqs;
}
