package com.example.webgrow.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String hostEmail;
    private Long capacityMin;
    private Long capacityMax;
    private LocalDateTime registerStart;
    private LocalDateTime registerEnd;
    private String festival;
    private String mode;
    private String imageUrl;
    private String eventType;
    private String url;
    private String organization;
}
