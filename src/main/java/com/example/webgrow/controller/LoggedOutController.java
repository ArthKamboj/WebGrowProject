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

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/public/homepage")
@RequiredArgsConstructor
public class LoggedOutController {

    private final EventService eventService;
    private final ParticipantService participantService;

    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> eventList = eventService.getUnloggedEvents();
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/events/details/{eventId}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventDetails(@PathVariable Long eventId, @AuthenticationPrincipal String email) {
        ApiResponse<EventDTO> eventDetails = participantService.getEventDetails(eventId);
        return ResponseEntity.ok(eventDetails);
    }
}