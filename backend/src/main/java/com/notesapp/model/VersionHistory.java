package com.notesapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "version_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VersionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "version_at", nullable = false)
    private Instant versionAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    private User editor;

    public VersionHistory() {}

    public VersionHistory(Note note, String title, String content, User editor) {
        this.note = note;
        this.title = title;
        this.content = content;
        this.editor = editor;
    }

    public Long getId() { return id; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getVersionAt() { return versionAt; }
    public User getEditor() { return editor; }
    public void setEditor(User editor) { this.editor = editor; }
}
