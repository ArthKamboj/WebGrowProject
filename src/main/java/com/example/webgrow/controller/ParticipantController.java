package com.example.webgrow.controller;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.NotificationDTO;
import com.example.webgrow.payload.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<EventDTO>> getRegisteredEvents(@AuthenticationPrincipal String email) {
        List<EventDTO> events = participantService.getRegisteredEvents(email);
        return ResponseEntity.ok(events);
    }

    // 3. Register for an Event
    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<String> registerForEvent(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        String message = participantService.registerForEvent(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 4. Add to Favourites
    @PostMapping("/events/{eventId}/favourites")
    public ResponseEntity<String> addToFavourites(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        String message = participantService.addToFavourites(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 5. Get Favourite Events
    @GetMapping("/events/favourites")
    public ResponseEntity<List<EventDTO>> getFavouriteEvents(@AuthenticationPrincipal String email) {
        List<EventDTO> events = participantService.getFavouriteEvents(email);
        return ResponseEntity.ok(events);
    }

    // 6. Unregister from an Event
    @DeleteMapping("/events/{eventId}/unregister")
    public ResponseEntity<String> unregisterFromEvent(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        String message = participantService.unregisterFromEvent(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 7. Unmark Event as Favourite
    @DeleteMapping("/events/{eventId}/favourites")
    public ResponseEntity<String> unmarkAsFavourite(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        String message = participantService.unmarkAsFavourite(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 8. Get Participant Profile
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal String email) {
        UserDTO participant = participantService.getParticipantProfile(email);
        return ResponseEntity.ok(participant);
    }

    // 9. Update Participant Profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal String email, @RequestBody User updatedProfile) {
        participantService.updateParticipantProfile(email, updatedProfile);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // 10. Get Notifications
    @GetMapping("/notifications/{page}/{size}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal String email,
                                                                  @PathVariable(required = false) Integer page,
                                                                  @PathVariable(required = false) Integer size)
    {
        int defaultPage = (page != null) ? page : 0;
        int defaultSize = (size != null) ? size : 10;

        List<NotificationDTO> notifications = participantService.getNotifications(email, defaultPage, defaultSize);
        return ResponseEntity.ok(notifications);
    }
}
