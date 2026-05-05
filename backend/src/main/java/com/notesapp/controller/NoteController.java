package com.notesapp.controller;

import com.notesapp.model.*;
import com.notesapp.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final VersionHistoryRepository versionHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final SharedNoteRepository sharedNoteRepository;

    public NoteController(NoteRepository noteRepository,
                          UserRepository userRepository,
                          TagRepository tagRepository,
                          VersionHistoryRepository versionHistoryRepository,
                          NotificationRepository notificationRepository,
                          SharedNoteRepository sharedNoteRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.versionHistoryRepository = versionHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.sharedNoteRepository = sharedNoteRepository;
    }

    @GetMapping
    public List<Note> listNotes(@RequestParam(required = false) Long ownerId,
                                @RequestParam(required = false) String query,
                                @RequestParam(required = false) String tag) {
        if (query != null && !query.isBlank()) {
            return noteRepository.search(query);
        }
        if (ownerId != null) {
            return userRepository.findById(ownerId).map(user -> {
                List<Note> owned = noteRepository.findByOwner(user);
                List<Note> shared = sharedNoteRepository.findByTargetUser(user).stream()
                        .map(SharedNote::getNote)
                        .collect(Collectors.toList());
                List<Note> all = new ArrayList<>(owned);
                all.addAll(shared);
                // Remove duplicates if any
                return all.stream().distinct().collect(Collectors.toList());
            }).orElse(List.of());
        }
        if (tag != null && !tag.isBlank()) {
            return noteRepository.findAll().stream()
                    .filter(n -> n.getTags().stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag)))
                    .collect(Collectors.toList());
        }
        return noteRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Map<String, Object> payload) {
        Long ownerId = payload.get("ownerId") instanceof Number ? ((Number) payload.get("ownerId")).longValue() : null;
        String title = (String) payload.getOrDefault("title", "Untitled note");
        String content = (String) payload.getOrDefault("content", "");
        List<String> tags = (List<String>) payload.getOrDefault("tags", List.of());

        User owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ownerId is required"));
        }

        Note note = new Note(owner, title, content);
        note.setTags(buildTags(tags));
        noteRepository.save(note);
        saveVersion(note, owner);
        sendNotification(owner, "Your note has been created.");
        return ResponseEntity.ok(note);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return noteRepository.findById(id).map(note -> {
            String title = (String) payload.getOrDefault("title", note.getTitle());
            String content = (String) payload.getOrDefault("content", note.getContent());
            Boolean favorite = payload.get("favorite") instanceof Boolean ? (Boolean) payload.get("favorite") : note.isFavorite();
            List<String> tags = (List<String>) payload.getOrDefault("tags", note.getTags().stream().map(Tag::getName).collect(Collectors.toList()));

            note.setTitle(title);
            note.setContent(content);
            note.setFavorite(favorite);
            note.setTags(buildTags(tags));
            note.setUpdatedAt(Instant.now());
            noteRepository.save(note);
            saveVersion(note, note.getOwner());
            sendNotification(note.getOwner(), "Your note was updated.");
            return ResponseEntity.ok(note);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "deleted"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<?> versions(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(versionHistoryRepository.findByNoteOrderByVersionAtDesc(note)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/versions/restore")
    public ResponseEntity<?> restoreVersion(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Long versionId = payload.get("versionId") instanceof Number ? ((Number) payload.get("versionId")).longValue() : null;
        return versionHistoryRepository.findById(versionId).map(version -> {
            Note note = version.getNote();
            note.setTitle(version.getTitle());
            note.setContent(version.getContent());
            note.setUpdatedAt(Instant.now());
            noteRepository.save(note);
            saveVersion(note, version.getEditor());
            return ResponseEntity.ok(note);
        }).orElse(ResponseEntity.notFound().build());
    }

    private Set<Tag> buildTags(List<String> names) {
        return names.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::strip)
                .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(new Tag(name))))
                .collect(Collectors.toSet());
    }

    private void saveVersion(Note note, User editor) {
        VersionHistory version = new VersionHistory(note, note.getTitle(), note.getContent(), editor);
        versionHistoryRepository.save(version);
    }

    private void sendNotification(User user, String message) {
        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);
    }
}
