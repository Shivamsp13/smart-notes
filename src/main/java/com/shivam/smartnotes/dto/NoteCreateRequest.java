package com.shivam.smartnotes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class NoteCreateRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

}
