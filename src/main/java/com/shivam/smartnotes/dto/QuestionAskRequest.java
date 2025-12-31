package com.shivam.smartnotes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionAskRequest {

    @NotBlank(message = "Question cannot be empty")
    private String question;
}
