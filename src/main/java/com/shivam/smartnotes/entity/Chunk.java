package com.shivam.smartnotes.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(
        name = "chunks",
        indexes = {
                @Index(name = "idx_chunk_note", columnList = "note_id"),
                @Index(name = "idx_chunk_order", columnList = "order_index")
        }
)
@Data
public class Chunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chunkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    private Notes note;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String chunkText;

    @Column(nullable = false)
    private Long orderIndex;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String embedding;

    public Chunk() {
        // JPA only
    }

    public Chunk(
            Notes note,
            String chunkText,
            Long orderIndex,
            String keywords,
            String embedding
    ) {
        this.note = note;
        this.chunkText = chunkText;
        this.orderIndex = orderIndex;
        this.keywords = keywords;
        this.embedding = embedding;
    }

//    public Notes getNotes() {
//        return note;
//    }
//
//    public String getEmbedding() {
//        return embedding;
//    }
//
//    public void setNotes(Notes note) {
//        this.note = note;
//    }
}
