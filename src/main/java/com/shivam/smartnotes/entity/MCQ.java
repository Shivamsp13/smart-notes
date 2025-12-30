package com.shivam.smartnotes.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(
        name = "mcqs",
        indexes = {
                @Index(name = "idx_mcq_note", columnList = "note_id"),
                @Index(name = "idx_mcq_topic", columnList = "topic")
        }
)
public class MCQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mcqId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @ElementCollection
    @CollectionTable(
            name = "mcq_options",
            joinColumns = @JoinColumn(name = "mcq_id")
    )
    @Column(name = "option_text", nullable = false)
    private List<String> options;

    @Column(nullable = false)
    private int correctOptionIndex;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    private Notes note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chunk_id")
    private Chunk chunk;

    protected MCQ() {
        // JPA only
    }

    public MCQ(
            String topic,
            String question,
            List<String> options,
            int correctOptionIndex,
            String explanation,
            Notes note,
            Chunk chunk
    ) {
        this.topic = topic;
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.note = note;
        this.chunk = chunk;
    }

    public Long getMcqId() {
        return mcqId;
    }

    public String getTopic() {
        return topic;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public Notes getNote() {
        return note;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
