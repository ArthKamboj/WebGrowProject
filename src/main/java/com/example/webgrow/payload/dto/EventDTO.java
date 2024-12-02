package com.example.webgrow.payload.dto;

import com.example.webgrow.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private boolean teamCreationAllowed;
    private int minTeamSize;
    private int maxTeamSize;
    private String url;
    private List<TimelineEntryDto> timelineEntries;
    private List<User> administrators = new ArrayList<>();
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
        private String imageUrl;
    }
}
