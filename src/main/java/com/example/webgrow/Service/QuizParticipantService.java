package com.example.webgrow.Service;

import com.example.webgrow.payload.dto.*;

import java.util.List;

public interface QuizParticipantService {

    List<QuizDTO> getAvailableQuizzes(String email);

    QuestionDTO getQuizQuestion(Long quizId, int page, String email);

    void submitAnswer(String email, QuizAnswerDTO quizAnswerDTO);

    void submitQuiz(Long quizId, String email);

    QuizAttemptDTO getQuizResults(String email, Long quizId);
}
