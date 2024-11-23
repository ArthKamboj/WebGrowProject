package com.example.webgrow.payload.dto;

import lombok.Data;

@Data
public class QuestionScoreDTO {

    private String questionText;
    private String selectedOption;
    private String correctAnswer;
    private boolean isCorrect;
}
