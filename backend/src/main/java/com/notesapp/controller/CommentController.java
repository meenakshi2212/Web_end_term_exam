package com.notesapp.controller;

import com.notesapp.model.Comment;
import com.notesapp.model.Note;
import com.notesapp.model.User;
import com.notesapp.repository.CommentRepository;
import com.notesapp.repository.NoteRepository;
import com.notesapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository,
                             NoteRepository noteRepository,
                             UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long noteId) {
        return noteRepository.findById(noteId)
                .map(note -> ResponseEntity.ok(commentRepository.findByNoteOrderByCreatedAtDesc(note)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> payload) {
        Long noteId = payload.get("noteId") instanceof Number ? ((Number) payload.get("noteId")).longValue() : null;
        Long authorId = payload.get("authorId") instanceof Number ? ((Number) payload.get("authorId")).longValue() : null;
        String text = (String) payload.get("text");

        Note note = noteRepository.findById(noteId).orElse(null);
        User author = userRepository.findById(authorId).orElse(null);
        if (note == null || author == null || text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "noteId, authorId and text are required"));
        }
        Comment comment = new Comment(note, author, text);
        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }
}
