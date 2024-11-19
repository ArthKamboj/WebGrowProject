package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.QuizParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.*;
import com.example.webgrow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizParticipantServiceImpl implements QuizParticipantService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public List<QuizDTO> getAvailableQuizzes(String email) {
        User user = getUserByEmail(email);
        LocalDateTime now = LocalDateTime.now();

        return quizRepository.findByParticipantsContaining(user)
                .stream()
                .filter(quiz -> quiz.getStartTime().isBefore(now) && quiz.getEndTime().isAfter(now))
                .map(this::convertToQuizDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDTO getQuizQuestion(Long quizId, int questionNumber, String email) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }

        Question question = questionRepository.findByQuizAndQuestionNumber(quiz, questionNumber)
                .orElseThrow(() -> new RuntimeException("Question not found for this quiz"));

        return convertToQuestionDTO(question);
    }

    @Override
    public void submitAnswer(String email, QuizAnswerDTO quizAnswerDTO) {
        User user = getUserByEmail(email);
        Question question = questionRepository.findById(quizAnswerDTO.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Quiz quiz = question.getQuiz();
        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }

        QuizAnswer answer = QuizAnswer.builder()
                .participant(user)
                .question(question)
                .selectedOption(quizAnswerDTO.getSelectedOption())
                .timestamp(LocalDateTime.now())
                .build();

        quizAnswerRepository.save(answer);
    }

    @Override
    public void submitQuiz(Long quizId, String email) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }


        List<QuizAnswer> answers = quizAnswerRepository.findByParticipantAndQuestionQuiz(user, quiz);
        long totalQuestions = questionRepository.countByQuiz(quiz);
        long correctAnswers = answers.stream()
                .filter(answer -> answer.getSelectedOption().equals(answer.getQuestion().getCorrectAnswer()))
                .count();

        QuizAttempt attempt = QuizAttempt.builder()
                .participant(user)
                .quiz(quiz)
                .totalQuestions((int) totalQuestions)
                .correctAnswers((int) correctAnswers)
                .attemptTime(LocalDateTime.now())
                .build();

        quizAttemptRepository.save(attempt);

        QuizAttemptDTO.builder()
                .quizId(quiz.getId())
                .title(quiz.getTitle())
                .totalQuestions((int) totalQuestions)
                .correctAnswers((int) correctAnswers)
                .attemptTime(attempt.getAttemptTime())
                .build();
    }

    @Override
    public QuizAttemptDTO getQuizResults(String email, Long quizId) {
        User user = getUserByEmail(email);

        QuizAttempt attempt = quizAttemptRepository.findByParticipantAndQuiz(user, quizId)
                .orElseThrow(() -> new RuntimeException("No quiz attempt found for this quiz and participant"));

        return convertToQuizAttemptDTO(attempt);
    }


    private QuizDTO convertToQuizDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .build();
    }

    private QuestionDTO convertToQuestionDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .options(question.getOptions())
                .build();
    }

    private QuizAttemptDTO convertToQuizAttemptDTO(QuizAttempt attempt) {
        return QuizAttemptDTO.builder()
                .quizId(attempt.getQuiz().getId())
                .title(attempt.getQuiz().getTitle())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .attemptTime(attempt.getAttemptTime())
                .build();
    }
}
