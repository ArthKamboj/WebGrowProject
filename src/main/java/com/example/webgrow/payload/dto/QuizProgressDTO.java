package com.example.webgrow.payload.dto;

import lombok.Data;

@Data
public class QuizProgressDTO {
    private long totalQuestions;
    private long submittedQuestions;
}
