package com.example.webgrow.controller;


import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {
        List<EventDTO> events = participantService.getAllEvents(search, category, location);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<String> registerForEvent(
            @PathVariable Long eventId, @RequestParam Integer Id) {
        participantService.registerForEvent(eventId, Id);
        return ResponseEntity.ok("Registered successfully.");
    }

    @GetMapping("/events/registered")
    public ResponseEntity<List<EventDTO>> getRegisteredEvents(@RequestParam Integer Id) {
        List<EventDTO> registeredEvents = participantService.getRegisteredEvents(Id);
        return ResponseEntity.ok(registeredEvents);
    }

    @PostMapping("/events/{eventId}/favorite")
    public ResponseEntity<String> markEventAsFavorite(
            @PathVariable Long eventId, @RequestParam Integer Id) {
        participantService.markEventAsFavorite(eventId, Id);
        return ResponseEntity.ok("Event marked as favorite.");
    }

    @DeleteMapping("/events/{eventId}/favorite")
    public ResponseEntity<String> unmarkEventAsFavorite(
            @PathVariable Long eventId, @RequestParam Integer Id) {
        participantService.unmarkEventAsFavorite(eventId, Id);
        return ResponseEntity.ok("Event unmarked as favorite.");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestParam Integer Id) {
        UserDTO user = participantService.getProfile(Id);
        return ResponseEntity.ok(user);
    }

}
