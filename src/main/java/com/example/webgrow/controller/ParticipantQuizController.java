package com.example.webgrow.controller;

import com.example.webgrow.Service.QuizParticipantService;
import com.example.webgrow.payload.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/participant/quiz")
@RequiredArgsConstructor
public class ParticipantQuizController {

    private final QuizParticipantService quizParticipantService;

    // 1. Get available quizzes for a participant
    @GetMapping("/available")
    public ResponseEntity<List<QuizDTO>> getAvailableQuizzes(@AuthenticationPrincipal String email) {
        List<QuizDTO> quizzes = quizParticipantService.getAvailableQuizzes(email);
        return ResponseEntity.ok(quizzes);
    }

    // 2. Get quiz questions (1 question at a time)
    @GetMapping("/{quizId}/questions/{page}")
    public ResponseEntity<QuestionDTO> getQuizQuestion(
            @PathVariable Long quizId,
            @PathVariable int page,
            @AuthenticationPrincipal String email
    ) {
        QuestionDTO question = quizParticipantService.getQuizQuestion(quizId, page, email);
        return ResponseEntity.ok(question);
    }

    // 3. Submit an answer to a question
    @PostMapping("/{quizId}/questions/{questionId}/answer")
    public ResponseEntity<String> submitAnswer(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody QuizAnswerDTO answerDTO,
            @AuthenticationPrincipal String email
    ) {
        answerDTO.setQuizId(String.valueOf(quizId));
        answerDTO.setQuestionId(String.valueOf(questionId));
        quizParticipantService.submitAnswer(email, answerDTO);
        return ResponseEntity.ok("Answer submitted successfully");
    }

    // 4. Complete the quiz
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<String> submitQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal String email
    ) {
        quizParticipantService.submitQuiz(quizId, email);
        return ResponseEntity.ok("Quiz completed successfully");
    }

    // 5. Get quiz results
    @GetMapping("/{quizId}/results")
    public ResponseEntity<QuizAttemptDTO> getQuizResults(
            @PathVariable Long quizId,
            @AuthenticationPrincipal String email
    ) {
        QuizAttemptDTO results = quizParticipantService.getQuizResults(email, quizId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{quizId}/leaderboard-and-scores")
    public ResponseEntity<LeaderboardResponseDTO> getLeaderboardAndParticipantDetails(
            @PathVariable Long quizId,
            @AuthenticationPrincipal String email
    ) {
        LeaderboardResponseDTO response = quizParticipantService.getLeaderboardAndParticipantDetails(quizId, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/progress")
    public ResponseEntity<QuizProgressDTO> getQuizProgress(
            @PathVariable Long quizId,
            @AuthenticationPrincipal String email
    ) {
        QuizProgressDTO progress = quizParticipantService.getQuizProgress(quizId, email);
        return ResponseEntity.ok(progress);
    }


}
