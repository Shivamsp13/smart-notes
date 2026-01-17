package com.shivam.smartnotes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MCQGenerateRequest {

    @NotNull(message = "noteId is required")
    private Long noteId;

    @NotBlank(message = "The topic cannot be blank")
    private String topic;

    @Min(1)
    private int count;
}
