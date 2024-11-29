package com.example.webgrow.controller;


import com.example.webgrow.Service.QuizHostService;
import com.example.webgrow.payload.dto.QuestionDTO;
import com.example.webgrow.payload.dto.QuizAttemptDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/host/quiz")
public class HostQuizController {

    private final QuizHostService quizHostService;

    @PostMapping("/{quizId}/add-questions")
    public ResponseEntity<String> addQuestionsToQuiz(
            @PathVariable Long quizId,
            @RequestBody List<QuestionDTO> questionDTOList
    ) {
        quizHostService.addQuestionsToQuiz(quizId, questionDTOList);
        return ResponseEntity.ok("Questions added successfully!");
    }

    @GetMapping("/quizzes/{quizId}/attempts")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizAttempts(@PathVariable Long quizId) {
        List<QuizAttemptDTO> attempts = quizHostService.getQuizAttempts(quizId);
        return ResponseEntity.ok(attempts);
    }
}
