package com.notesapp.controller;

import com.notesapp.model.*;
import com.notesapp.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupController(GroupRepository groupRepository,
                           UserRepository userRepository,
                           GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<GroupEntity>> getGroups(@PathVariable Long ownerId) {
        return userRepository.findById(ownerId)
                .map(owner -> ResponseEntity.ok(groupRepository.findByOwner(owner)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> payload) {
        Long ownerId = payload.get("ownerId") instanceof Number ? ((Number) payload.get("ownerId")).longValue() : null;
        String name = (String) payload.get("name");
        User owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null || name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "ownerId and name are required"));
        }
        GroupEntity group = new GroupEntity(name.strip(), owner);
        groupRepository.save(group);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long groupId, @RequestBody Map<String, Object> payload) {
        Long userId = payload.get("userId") instanceof Number ? ((Number) payload.get("userId")).longValue() : null;
        String role = (String) payload.getOrDefault("role", "VIEWER");

        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (group == null || user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "groupId and userId are required"));
        }
        GroupMember member = new GroupMember(group, user, role);
        groupMemberRepository.save(member);
        return ResponseEntity.ok(member);
    }
}
