package com.notesapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shared_notes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SharedNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_group_id")
    private GroupEntity targetGroup;

    @Column(nullable = false)
    private String permission = "EDITOR";

    @Column(name = "shared_at", nullable = false)
    private Instant sharedAt = Instant.now();

    public SharedNote() {}

    public SharedNote(Note note, User targetUser, GroupEntity targetGroup, String permission) {
        this.note = note;
        this.targetUser = targetUser;
        this.targetGroup = targetGroup;
        this.permission = permission;
    }

    public Long getId() { return id; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public User getTargetUser() { return targetUser; }
    public void setTargetUser(User targetUser) { this.targetUser = targetUser; }
    public GroupEntity getTargetGroup() { return targetGroup; }
    public void setTargetGroup(GroupEntity targetGroup) { this.targetGroup = targetGroup; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
