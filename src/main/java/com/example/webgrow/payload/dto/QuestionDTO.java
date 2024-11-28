package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private String id;
    private Long quizId;
    private String questionText;
    private List<String> options;
    private int pageNumber;
    private int totalPages;
    private String correctAnswer;
}
