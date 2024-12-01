package com.example.webgrow.controller;


import com.example.webgrow.Service.EventService;
import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.payload.dto.ApiResponse;
import com.example.webgrow.payload.dto.EventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/public/homepage")
@RequiredArgsConstructor
public class LoggedOutController {

    private final EventService eventService;
    private final ParticipantService participantService;

    @GetMapping("/{page}/{size}")
    public Page<EventDTO> getPaginatedEvents(
            @PathVariable int page,
            @PathVariable int size) {
        return eventService.getUnloggedEvents(page, size);
    }

    @GetMapping("/events/details/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventDetails(@PathVariable Long eventId, @AuthenticationPrincipal String email) {
        ApiResponse<EventDTO> eventDetails = participantService.getEventDetails(eventId);
        return ResponseEntity.ok(eventDetails);
    }
}
