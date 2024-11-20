package com.example.webgrow.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime time;
    private String hostEmail;
}
