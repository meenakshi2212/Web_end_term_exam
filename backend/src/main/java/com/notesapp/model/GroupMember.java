package com.notesapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "group_members")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role = "VIEWER";

    public GroupMember() {}

    public GroupMember(GroupEntity group, User user, String role) {
        this.group = group;
        this.user = user;
        this.role = role;
    }

    public Long getId() { return id; }
    public GroupEntity getGroup() { return group; }
    public void setGroup(GroupEntity group) { this.group = group; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
