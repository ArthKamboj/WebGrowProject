package com.example.webgrow.controller;


import com.example.webgrow.Service.EventService;
import com.example.webgrow.payload.dto.EventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/public/homepage")
@RequiredArgsConstructor
public class LoggedOutController {

    private final EventService eventService;

    @GetMapping("/{page}/{size}")
    public Page<EventDTO> getPaginatedEvents(
            @PathVariable int page,
            @PathVariable int size) {
        return eventService.getUnloggedEvents(page, size);
    }
}
