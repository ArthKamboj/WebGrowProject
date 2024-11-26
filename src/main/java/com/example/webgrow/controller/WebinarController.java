package com.example.webgrow.controller;

import com.example.webgrow.Service.WebinarService;
import com.example.webgrow.models.Webinar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/webinars")
public class WebinarController {
    private final WebinarService webinarService;

    public WebinarController(WebinarService webinarService) {
        this.webinarService = webinarService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Webinar>> getAllWebinars() {
        return ResponseEntity.ok(webinarService.getAllWebinars());
    }

    @PostMapping("/create")
    public ResponseEntity<Webinar> createWebinar(@RequestBody Webinar webinar) {
        return ResponseEntity.ok(webinarService.createWebinar(webinar));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Webinar> getWebinarById(@PathVariable Long id) {
        return ResponseEntity.ok(webinarService.getWebinarById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteWebinar(@PathVariable Long id) {
        webinarService.deleteWebinar(id);
        return ResponseEntity.noContent().build();
    }
}
