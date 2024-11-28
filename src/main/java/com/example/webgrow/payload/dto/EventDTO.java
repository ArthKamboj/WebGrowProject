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

    private String id;
    private String title;
    private String description;

    private String location;
    private String category;
    private Long capacityMin;
    private Long capacityMax;
    private LocalDateTime registerStart;
    private LocalDateTime registerEnd;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdate;

    private String mode;
    private String imageUrl;

    private boolean isActive;
    private String url;

    private HostDTO host;

    @Data
    public static class HostDTO {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String mobile;
        private String organization;
        private String designation;
    }
}
