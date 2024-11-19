package com.example.webgrow.payload.request;

import lombok.Data;

@Data
public class EventRequest {
    private String imageUrl;
    private String title;
    private String description;
    private String location;
    private String mode;
    private String participationType;
    private Long duration;
    private Long participationNumber;
}
