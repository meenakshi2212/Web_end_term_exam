package com.notesapp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class RealtimeController {

    @MessageMapping("/notes/update")
    @SendTo("/topic/notes")
    public Map<String, Object> broadcastNoteUpdate(@Payload Map<String, Object> payload) {
        return payload;
    }
}
