package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String category;
    private Long capacity;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String mode;
    private String imageUrl;

    private boolean isActive;
}
