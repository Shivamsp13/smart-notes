package com.shivam.smartnotes.dto;

import com.shivam.smartnotes.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteCreateRequest {
    private Long noteId;
    private User owner;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

}
