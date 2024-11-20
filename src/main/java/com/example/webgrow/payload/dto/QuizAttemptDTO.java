package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDTO {
    private Long quizId;
    private String title;
    private String participantName;
    private int totalQuestions;
    private int correctAnswers;
    private LocalDateTime attemptTime;
    private String status;
}
