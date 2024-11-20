//package com.example.webgrow.Service.Impl;
//
//import com.example.webgrow.Service.QuizParticipantService;
//import com.example.webgrow.models.*;
//import com.example.webgrow.payload.dto.*;
//import com.example.webgrow.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class QuizParticipantServiceImpl implements QuizParticipantService {
//
//    private final QuizRepository quizRepository;
//    private final QuestionRepository questionRepository;
//    private final QuizAttemptRepository quizAttemptRepository;
//    private final QuizAnswerRepository quizAnswerRepository;
//    private final UserRepository userRepository;
//
//    private User getUserByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
//    }
//
//    @Override
//    public List<QuizDTO> getAvailableQuizzes(String email) {
//        User user = getUserByEmail(email);
//        LocalDateTime now = LocalDateTime.now();
//
//        return quizRepository.findByParticipantsContaining(user)
//                .stream()
//                .filter(quiz -> quiz.getStartTime().isBefore(now) && quiz.getEndTime().isAfter(now))
//                .map(this::convertToQuizDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public QuestionDTO getQuizQuestion(Long quizId, int questionNumber, String email) {
//        User user = getUserByEmail(email);
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new RuntimeException("Quiz not found"));
//
//        if (!quiz.getParticipants().contains(user)) {
//            throw new RuntimeException("Participant is not registered for this quiz");
//        }
//
//        Question question = questionRepository.findByQuizAndQuestionNumber(quiz, questionNumber)
//                .orElseThrow(() -> new RuntimeException("Question not found for this quiz"));
//
//        return convertToQuestionDTO(question);
//    }
//
//    @Override
//    public void submitAnswer(String email, QuizAnswerDTO quizAnswerDTO) {
//        User user = getUserByEmail(email);
//        Question question = questionRepository.findById(quizAnswerDTO.getQuestionId())
//                .orElseThrow(() -> new RuntimeException("Question not found"));
//
//        Quiz quiz = question.getQuiz();
//        if (!quiz.getParticipants().contains(user)) {
//            throw new RuntimeException("Participant is not registered for this quiz");
//        }
//
//        QuizAnswer answer = new QuizAnswer();
//        answer.setParticipant(user);
//        answer.setQuestion(question);
//        answer.setSelectedOption(quizAnswerDTO.getSelectedOption());
//        answer.setTimestamp(LocalDateTime.now());
//
//        quizAnswerRepository.save(answer);
//    }
//
//    @Override
//    public void submitQuiz(Long quizId, String email) {
//        User user = getUserByEmail(email);
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new RuntimeException("Quiz not found"));
//
//        if (!quiz.getParticipants().contains(user)) {
//            throw new RuntimeException("Participant is not registered for this quiz");
//        }
//
//        List<QuizAnswer> answers = quizAnswerRepository.findByParticipantAndQuestionQuiz(user, quiz);
//        long totalQuestions = questionRepository.countByQuiz(quiz);
//        long correctAnswers = answers.stream()
//                .filter(answer -> answer.getSelectedOption().equals(answer.getQuestion().getCorrectAnswer()))
//                .count();
//
//        QuizAttempt attempt = new QuizAttempt();
//        attempt.setParticipant(user);
//        attempt.setQuiz(quiz);
//        attempt.setTotalQuestions((int) totalQuestions);
//        attempt.setCorrectAnswers((int) correctAnswers);
//        attempt.setAttemptTime(LocalDateTime.now());
//
//        quizAttemptRepository.save(attempt);
//    }
//
//    @Override
//    public QuizAttemptDTO getQuizResults(String email, Long quizId) {
//        User user = getUserByEmail(email);
//
//        QuizAttempt attempt = quizAttemptRepository.findByParticipantAndQuiz(user, quizId)
//                .orElseThrow(() -> new RuntimeException("No quiz attempt found for this quiz and participant"));
//
//        return convertToQuizAttemptDTO(attempt);
//    }
//
//    private QuizDTO convertToQuizDTO(Quiz quiz) {
//        QuizDTO quizDTO = new QuizDTO();
//        quizDTO.setId(quiz.getId());
//        quizDTO.setTitle(quiz.getTitle());
//        quizDTO.setDescription(quiz.getDescription());
//        quizDTO.setStartTime(quiz.getStartTime());
//        quizDTO.setEndTime(quiz.getEndTime());
//        return quizDTO;
//    }
//
//    private QuestionDTO convertToQuestionDTO(Question question) {
//        QuestionDTO questionDTO = new QuestionDTO();
//        questionDTO.setId(question.getId());
//        questionDTO.setQuestionText(question.getQuestionText());
//        questionDTO.setOptions(question.getOptions());
//        return questionDTO;
//    }
//
//    private QuizAttemptDTO convertToQuizAttemptDTO(QuizAttempt attempt) {
//        QuizAttemptDTO attemptDTO = new QuizAttemptDTO();
//        attemptDTO.setQuizId(attempt.getQuiz().getId());
//        attemptDTO.setTitle(attempt.getQuiz().getTitle());
//        attemptDTO.setTotalQuestions(attempt.getTotalQuestions());
//        attemptDTO.setCorrectAnswers(attempt.getCorrectAnswers());
//        attemptDTO.setAttemptTime(attempt.getAttemptTime());
//        return attemptDTO;
//    }
//}
