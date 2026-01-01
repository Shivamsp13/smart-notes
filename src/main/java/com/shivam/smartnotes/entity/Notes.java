package com.shivam.smartnotes.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
@Entity
@Table(name="notes")
@Data
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    //This is called cascasde deletion
    //When note is deleted then all chunks belonging to that note will be automatically deleted
    @OneToMany(
            mappedBy = "note",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Chunk> chunks = new ArrayList<>();

    //Many notes can belong to one user.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User owner;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    protected Notes(){
        //JPA only use
    }
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
