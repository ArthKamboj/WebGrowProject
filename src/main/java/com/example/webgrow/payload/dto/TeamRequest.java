package com.example.webgrow.payload.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequest {
    private String teamName;
    private boolean isPublic; // Indicates if the team is searchable.
}
