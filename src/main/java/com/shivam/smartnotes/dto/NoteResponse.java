package com.shivam.smartnotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class NoteResponse {

    private Long noteId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private String title;

}
