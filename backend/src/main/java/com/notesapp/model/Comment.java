package com.notesapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Comment() {}

    public Comment(Note note, User author, String text) {
        this.note = note;
        this.author = author;
        this.text = text;
    }

    public Long getId() { return id; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Instant getCreatedAt() { return createdAt; }
}
