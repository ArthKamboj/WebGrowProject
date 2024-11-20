package com.example.webgrow.controller;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.request.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<DTOClass> createEvent(@RequestBody EventRequest eventRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
        String email = authentication.getName();
//        String email = currentUser.getEmail();
        DTOClass response = eventService.createEvent(eventRequest,email);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<DTOClass> updateEvent(@RequestBody EventRequest eventRequest, @PathVariable("id") long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
        String email = authentication.getName();
        DTOClass response = eventService.updateEvent(id,eventRequest,email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DTOClass> deleteEvent(@PathVariable ("id") long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
        String email = authentication.getName();
        DTOClass response = eventService.deleteEvent(id,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<DTOClass> getAllEvents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
        String email = authentication.getName();
        DTOClass response = eventService.getEventList(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOClass> getEvent(@PathVariable ("id") long id) {
        DTOClass response = eventService.getEventDetails(id);
        return ResponseEntity.ok(response);
    }

}
