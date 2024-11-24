package com.example.webgrow.payload.dto;


import lombok.Data;

@Data
public class LeaderboardEntryDTO {

    private String participantName;
    private String imageUrl;
    private int correctAnswers;
    private int totalQuestions;
}
