package com.shivam.smartnotes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MCQGenerateRequest {

    @NotBlank(message = "The topic cannot be blank")
    private String topic;

    @Min(1)
    private int count;
}
