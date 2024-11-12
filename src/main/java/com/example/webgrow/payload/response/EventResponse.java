package com.example.webgrow.payload.response;

import lombok.Data;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
    private String hostEmail;
}
