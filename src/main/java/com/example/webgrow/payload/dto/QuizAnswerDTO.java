package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDTO {
    private Long quizId;
    private Long questionId;
    private String selectedOption;
}
