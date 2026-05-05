package com.notesapp.controller;

import com.notesapp.model.Notification;
import com.notesapp.model.User;
import com.notesapp.repository.NotificationRepository;
import com.notesapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(notificationRepository.findByUserOrderByCreatedAtDesc(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/seen")
    public ResponseEntity<?> markSeen(@PathVariable Long id) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setSeen(true);
            notificationRepository.save(notification);
            return ResponseEntity.ok(Map.of("message", "seen"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
