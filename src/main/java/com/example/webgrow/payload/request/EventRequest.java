package com.example.webgrow.payload.request;

import lombok.Data;

@Data
public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
}
