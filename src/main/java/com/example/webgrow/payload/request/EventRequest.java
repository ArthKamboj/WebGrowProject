package com.example.webgrow.payload.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(https?://)?[a-zA-Z0-9-._~:/?#@!$&'()*+,;=]+$",
            message = "Invalid URL format"
    )
    private String url;
    private String organization;
    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }
}
