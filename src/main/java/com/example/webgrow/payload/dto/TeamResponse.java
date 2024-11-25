package com.example.webgrow.payload.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private Long leaderId;
}
