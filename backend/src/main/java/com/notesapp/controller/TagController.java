package com.notesapp.controller;

import com.notesapp.model.Tag;
import com.notesapp.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<Tag> listTags() {
        return tagRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tag name required"));
        }
        if (tagRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tag already exists"));
        }
        Tag tag = new Tag(name.strip());
        tagRepository.save(tag);
        return ResponseEntity.ok(tag);
    }
}
