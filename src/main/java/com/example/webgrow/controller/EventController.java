package com.example.webgrow.controller;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.models.DTOClass;
import com.example.webgrow.payload.request.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    @PostMapping("/create")
    public ResponseEntity<DTOClass> createEvent(@RequestBody EventRequest eventRequest,
                                                @RequestParam String hostEmail) {
        DTOClass response = eventService.createEvent(eventRequest, hostEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DTOClass> updateEvent(@RequestBody EventRequest eventRequest,
                                                @PathVariable Long id,
                                                @RequestParam String hostEmail ) {
        DTOClass response = eventService.updateEvent(id,eventRequest,hostEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DTOClass> deleteEvent(@PathVariable Long id,
                                                @RequestParam String hostEmail) {
        DTOClass response = eventService.deleteEvent(id,hostEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<DTOClass> getAllEvents(@RequestParam String hostEmail) {
        DTOClass response = eventService.getEventList(hostEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOClass> getEvent(@PathVariable Long id) {
        DTOClass response = eventService.getEventDetails(id);
        return ResponseEntity.ok(response);
    }

}
