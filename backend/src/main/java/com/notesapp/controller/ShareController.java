package com.notesapp.controller;

import com.notesapp.model.Note;
import com.notesapp.model.SharedNote;
import com.notesapp.model.User;
import com.notesapp.repository.NoteRepository;
import com.notesapp.repository.SharedNoteRepository;
import com.notesapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/share")
@CrossOrigin(origins = "*")
public class ShareController {
    private final SharedNoteRepository sharedNoteRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public ShareController(SharedNoteRepository sharedNoteRepository,
                           NoteRepository noteRepository,
                           UserRepository userRepository) {
        this.sharedNoteRepository = sharedNoteRepository;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> shareNote(@RequestBody Map<String, Object> payload) {
        Long noteId = payload.get("noteId") instanceof Number ? ((Number) payload.get("noteId")).longValue() : null;
        String targetUsername = (String) payload.get("username");
        String permission = (String) payload.getOrDefault("permission", "EDITOR");

        Note note = noteRepository.findById(noteId).orElse(null);
        User targetUser = userRepository.findByUsername(targetUsername).orElse(null);

        if (note == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Note not found"));
        }
        if (targetUser == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User '" + targetUsername + "' not found"));
        }

        SharedNote sharedNote = new SharedNote(note, targetUser, null, permission);
        sharedNoteRepository.save(sharedNote);

        return ResponseEntity.ok(Map.of("message", "Note shared with " + targetUsername));
    }
}
