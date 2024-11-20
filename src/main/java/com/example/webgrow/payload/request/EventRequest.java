package com.example.webgrow.payload.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String imageUrl;
    private String title;
    private String description;
    private String location;
    private String mode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long capacity;

}
