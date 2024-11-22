package com.example.webgrow.controller;


import com.example.webgrow.Service.QuizHostService;
import com.example.webgrow.payload.dto.QuestionDTO;
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

    @PostMapping("/add-questions")
    public ResponseEntity<String> addQuestionsToQuiz(@RequestBody List<QuestionDTO> questionDTOList) {
        quizHostService.addQuestionsToQuiz(questionDTOList);
        return ResponseEntity.ok("Questions added successfully!");
    }
}
