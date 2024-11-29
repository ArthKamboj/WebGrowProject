package com.example.webgrow.payload.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private String id;
    private String name;
    private String leaderId;
    private String leaderName;
    private String leaderEmail;
}
