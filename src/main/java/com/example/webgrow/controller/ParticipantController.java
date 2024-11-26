package com.example.webgrow.controller;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.*;
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
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {

        search = search != null ? search : "";
        category = category != null ? category : "";
        location = location != null ? location : "";
        ApiResponse<List<EventDTO>> events = participantService.getAllEvents(search, category, location);
        return ResponseEntity.ok(events);
    }

    // 2. Get Registered Events
    @GetMapping("/events/registered")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getRegisteredEvents(@AuthenticationPrincipal String email) {
        ApiResponse<List<EventDTO>> events = participantService.getRegisteredEvents(email);
        return ResponseEntity.ok(events);
    }

    // 3. Register for an Event
    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<ApiResponse<String>> registerForEvent(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        ApiResponse<String> message = participantService.registerForEvent(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 4. Add to Favourites
    @PostMapping("/events/{eventId}/favourites")
    public ResponseEntity<ApiResponse<String>> addToFavourites(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        ApiResponse<String> message = participantService.addToFavourites(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 5. Get Favourite Events
    @GetMapping("/events/favourites")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getFavouriteEvents(@AuthenticationPrincipal String email) {
        ApiResponse<List<EventDTO>> events = participantService.getFavouriteEvents(email);
        return ResponseEntity.ok(events);
    }

    // 6. Unregister from an Event
    @DeleteMapping("/events/{eventId}/unregister")
    public ResponseEntity<ApiResponse<String>> unregisterFromEvent(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        ApiResponse<String> message = participantService.unregisterFromEvent(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 7. Unmark Event as Favourite
    @DeleteMapping("/events/{eventId}/favourites")
    public ResponseEntity<ApiResponse<String>> unmarkAsFavourite(@AuthenticationPrincipal String email, @PathVariable Long eventId) {
        ApiResponse<String> message = participantService.unmarkAsFavourite(email, eventId);
        return ResponseEntity.ok(message);
    }

    // 8. Get Participant Profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@AuthenticationPrincipal String email) {
        ApiResponse<UserDTO> participant = participantService.getParticipantProfile(email);
        return ResponseEntity.ok(participant);
    }

    // 9. Update Participant Profile
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@AuthenticationPrincipal String email, @RequestBody User updatedProfile) {
        ApiResponse<String> message = participantService.updateParticipantProfile(email, updatedProfile);
        return ResponseEntity.ok(message);
    }

    // 10. Get Notifications
    @GetMapping("/notifications/{page}/{size}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(@AuthenticationPrincipal String email,
                                                                  @PathVariable Integer page,
                                                                  @PathVariable Integer size)
    {
        int defaultPage = (page != null) ? page : 0;
        int defaultSize = (size != null) ? size : 10;

        ApiResponse<List<NotificationDTO>> notifications = participantService.getNotifications(email, defaultPage, defaultSize);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/events/details/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventDetails(@PathVariable Long eventId) {
        ApiResponse<EventDTO> eventDetails = participantService.getEventDetails(eventId);
        return ResponseEntity.ok(eventDetails);
    }

    @PostMapping("/events/{eventId}/teams/create")
    public ResponseEntity<ApiResponse<String>> createTeam(@AuthenticationPrincipal String email,
                                             @PathVariable Long eventId,
                                             @RequestBody TeamRequest teamRequest) {
        ApiResponse<String> response = participantService.createTeam(email, eventId, teamRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{eventId}/teams/search")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> searchTeams(@PathVariable Long eventId,
                                                          @RequestParam(required = false) String teamName) {
        ApiResponse<List<TeamResponse>> teams = participantService.searchTeams(eventId, teamName);
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/teams/{teamId}/join/request")
    public ResponseEntity<ApiResponse<String>> requestToJoinTeam(@AuthenticationPrincipal String email,
                                                    @PathVariable Long teamId) {
        ApiResponse<String> response = participantService.requestToJoinTeam(email, teamId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams/join/request/{requestId}")
    public ResponseEntity<ApiResponse<String>> respondToJoinRequest(@RequestParam String response,
                                                       @PathVariable Long requestId) {
        ApiResponse<String> result = participantService.respondToJoinRequest(requestId, response);
        return ResponseEntity.ok(result);
    }


}
