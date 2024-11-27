package com.example.webgrow.payload.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String imageUrl;
    private String title;
    private String description;
    private String location;
    private String category;
    private String mode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long capacityMin;
    private Long capacityMax;
    private LocalDateTime registerStart;
    private LocalDateTime registerEnd;
    private String festival;
    private String eventType;
    private boolean teamCreationAllowed;
    private int minTeamSize;
    private int maxTeamSize;
    private String url;
    private String organization;
}
