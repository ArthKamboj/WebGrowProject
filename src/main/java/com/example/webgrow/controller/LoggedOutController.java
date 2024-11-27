package com.example.webgrow.controller;


import com.example.webgrow.Service.EventService;
import com.example.webgrow.payload.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/homepage")
public class LoggedOutController {

    private EventService eventService;

    @GetMapping("/{page}/{size}")
    public Page<EventDTO> getPaginatedEvents(
            @PathVariable int page,
            @PathVariable int size) {
        return eventService.getUnloggedEvents(page, size);
    }
}
