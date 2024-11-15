package com.example.webgrow.controller;


import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.User;
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

    // 1. Get All Events
    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {
        List<EventDTO> events = participantService.getAllEvents(search, category, location);
        return ResponseEntity.ok(events);
    }

    // 2. Get Registered Events
    @GetMapping("/events/registered")
    public ResponseEntity<List<EventDTO>> getRegisteredEvents(@RequestParam Integer participantId) {
        List<EventDTO> events = participantService.getRegisteredEvents(participantId);
        return ResponseEntity.ok(events);
    }

    // 3. Register for an Event
    @PostMapping("/events/register")
    public ResponseEntity<String> registerForEvent(@RequestParam Integer participantId, @RequestParam Long eventId) {
        String message = participantService.registerForEvent(participantId, eventId);
        return ResponseEntity.ok(message);
    }

    // 4. Add to Favourites
    @PostMapping("/events/favourites")
    public ResponseEntity<String> addToFavourites(@RequestParam Integer participantId, @RequestParam Long eventId) {
        String message = participantService.addToFavourites(participantId, eventId);
        return ResponseEntity.ok(message);
    }

    // 5. Get Favourite Events
    @GetMapping("/events/favourites")
    public ResponseEntity<List<EventDTO>> getFavouriteEvents(@RequestParam Integer participantId) {
        List<EventDTO> events = participantService.getFavouriteEvents(participantId);
        return ResponseEntity.ok(events);
    }

    // 6. Unregister from an Event
    @DeleteMapping("/events/unregister")
    public ResponseEntity<String> unregisterFromEvent(@RequestParam Integer participantId, @RequestParam Long eventId) {
        String message = participantService.unregisterFromEvent(participantId, eventId);
        return ResponseEntity.ok(message);
    }

    // 7. Unmark Event as Favourite
    @DeleteMapping("/events/favourites")
    public ResponseEntity<String> unmarkAsFavourite(@RequestParam Integer participantId, @RequestParam Long eventId) {
        String message = participantService.unmarkAsFavourite(participantId, eventId);
        return ResponseEntity.ok(message);
    }

    // 8. Get Participant Profile
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam Integer participantId) {
        User participant = participantService.getParticipantProfile(participantId);
        return ResponseEntity.ok(participant);
    }

    // 9. Update Participant Profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestParam Integer participantId, @RequestBody User updatedProfile) {
        participantService.updateParticipantProfile(participantId, updatedProfile);
        return ResponseEntity.ok("Profile updated successfully");
    }



}