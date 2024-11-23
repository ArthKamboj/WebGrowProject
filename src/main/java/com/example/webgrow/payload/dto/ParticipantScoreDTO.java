package com.example.webgrow.payload.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParticipantScoreDTO {

    private String participantName;
    private String imageUrl;
    private int correctAnswers;
    private int totalQuestions;
    private List<QuestionScoreDTO> questionScores;
}
