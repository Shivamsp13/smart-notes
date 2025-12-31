package com.shivam.smartnotes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MCQSubmitRequest {

    @NotNull
    private Long userId;

    @NotEmpty
    @Valid
    private List<MCQAnswerRequest> answers;

}
