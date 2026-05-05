package com.notesapp.controller;

import com.notesapp.model.User;
import com.notesapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String displayName = payload.get("displayName");
        String password = payload.get("password");

        if (username == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username, email and password are required"));
        }

        if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username or email already taken"));
        }

        System.out.println("Creating user: " + username);
        User user = new User(username, email, displayName == null ? username : displayName, hash(password));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "account created", "userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
        }

        System.out.println("Login attempt for user: " + username);
        return userRepository.findByUsername(username)
                .filter(user -> user.getPasswordHash().equals(hash(password)))
                .map(user -> {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("userId", user.getId());
                    resp.put("username", user.getUsername());
                    resp.put("displayName", user.getDisplayName());
                    resp.put("avatarUrl", user.getAvatarUrl());
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "invalid credentials")));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
