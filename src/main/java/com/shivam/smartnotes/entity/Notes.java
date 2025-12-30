package com.shivam.smartnotes.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="notes")
@Data
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User owner;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public Notes(String title,String content,User owner){
        this.title=title;
        this.content=content;
        this.owner=owner;
    }
    public Notes(
            User owner,
            String title,
            String content,
            LocalDateTime expiredAt,
            LocalDateTime createdAt
    ) {
        this.owner = owner;
        this.title = title;
        this.content = content;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
    }
}
