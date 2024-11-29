package com.example.webgrow.payload.response;

import com.example.webgrow.payload.dto.TimelineEntryDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponse {
    private String id;
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
    private String category;
    private String url;
    private LocalDateTime lastUpdate;
    private String organization;
    private List<TimelineEntryDto> timelineEntries;
}
