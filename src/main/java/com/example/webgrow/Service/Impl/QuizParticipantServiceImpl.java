package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.QuizParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.*;
import com.example.webgrow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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
    public QuestionDTO getQuizQuestion(Long quizId, int page, String email) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }
        long totalQuestions = questionRepository.countByQuiz(quiz);

        if (page < 1 || page > totalQuestions) {
            throw new RuntimeException("Invalid page number. Must be between 1 and " + totalQuestions);
        }

        Pageable pageable = PageRequest.of(page - 1, 1);
        Page<Question> questionPage = questionRepository.findByQuiz(quiz, pageable);

        if (questionPage.isEmpty()) {
            throw new RuntimeException("No question found for this page");
        }

        return convertToQuestionDTO(questionPage.getContent().get(0));
    }

    @Override
    public void submitAnswer(String email, QuizAnswerDTO quizAnswerDTO) {
        User user = getUserByEmail(email);
        Question question = questionRepository.findById(Long.parseLong(quizAnswerDTO.getQuestionId()))
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Quiz quiz = question.getQuiz();
        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }

        if (!question.getOptions().contains(quizAnswerDTO.getSelectedOption())) {
            throw new RuntimeException("Selected option is not valid for this question!");
        }

        QuizAnswer answer = new QuizAnswer();
        answer.setParticipant(user);
        answer.setQuestion(question);
        answer.setSelectedOption(quizAnswerDTO.getSelectedOption());
        answer.setTimestamp(LocalDateTime.now());

        quizAnswerRepository.save(answer);
    }

    @Override
    public QuizProgressDTO getQuizProgress(Long quizId, String email) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));


        long totalQuestions = questionRepository.countByQuiz(quiz);


        long submittedQuestions = quizAnswerRepository.countByParticipantAndQuestionQuiz(user, quiz);


        QuizProgressDTO progressDTO = new QuizProgressDTO();
        progressDTO.setTotalQuestions(totalQuestions);
        progressDTO.setSubmittedQuestions(submittedQuestions);

        return progressDTO;
    }



    @Override
    public void submitQuiz(Long quizId, String email) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getParticipants().contains(user)) {
            throw new RuntimeException("Participant is not registered for this quiz");
        }

        if (quizAttemptRepository.findByParticipantAndQuiz(user, quiz).isPresent()) {
            throw new RuntimeException("Quiz has already been submitted by this participant!");
        }

        List<QuizAnswer> answers = quizAnswerRepository.findByParticipantAndQuestionQuiz(user, quiz);
        long totalQuestions = questionRepository.countByQuiz(quiz);
        long correctAnswers = answers.stream()
                .filter(QuizAnswer::isCorrect)
                .count();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setParticipant(user);
        attempt.setQuiz(quiz);
        attempt.setTotalQuestions((int) totalQuestions);
        attempt.setCorrectAnswers((int) correctAnswers);
        attempt.setAttemptTime(LocalDateTime.now());
        attempt.setCompleted(true);

        quizAttemptRepository.save(attempt);

        quiz.setCompleted(true);
        quizRepository.save(quiz);

        long coins = user.getCoins();
        coins += 10L;
        user.setCoins(coins);
    }


    @Override
    public QuizAttemptDTO getQuizResults(String email, Long quizId) {
        User user = getUserByEmail(email);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));


        QuizAttempt attempt = quizAttemptRepository.findByParticipantAndQuiz(user, quiz)
                .orElseThrow(() -> new RuntimeException("No quiz attempt found for this quiz and participant"));

        int rank = calculateRank(quizId, attempt.getCorrectAnswers(), attempt.getAttemptTime());

        QuizAttemptDTO dto = convertToQuizAttemptDTO(attempt);
        dto.setRank(rank);

        return dto;
    }

    private int calculateRank(Long quizId, int correctAnswers, LocalDateTime attemptTime) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdOrderByCorrectAnswersDescAttemptTimeAsc(quizId);
        int rank = 1; for (QuizAttempt attempt : attempts) {
            if (attempt.getCorrectAnswers() > correctAnswers ||
                    (attempt.getCorrectAnswers() == correctAnswers && attempt.getAttemptTime().isBefore(attemptTime))) {
                rank++;
            }
        }
        return rank;
    }

    @Override
    public LeaderboardResponseDTO getLeaderboardAndParticipantDetails(Long quizId, String email) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));


        List<QuizAttempt> allAttempts = quizAttemptRepository
                .findByQuizOrderByCorrectAnswersDescAttemptTimeAsc(quiz);
        List<LeaderboardEntryDTO> allScores = allAttempts.stream()
                .map(this::convertToLeaderboardEntryDTO)
                .collect(Collectors.toList());


        User participant = getUserByEmail(email);
        QuizAttempt participantAttempt = quizAttemptRepository.findByParticipantAndQuiz(participant, quiz)
                .orElseThrow(() -> new RuntimeException("No quiz attempt found for this participant"));

        ParticipantScoreDTO participantDetails = convertToParticipantScoreDTO(participantAttempt);


        LeaderboardResponseDTO response = new LeaderboardResponseDTO();
        response.setTopScores(allScores);
        response.setParticipantDetails(participantDetails);
        return response;
    }

    private LeaderboardEntryDTO convertToLeaderboardEntryDTO(QuizAttempt attempt) {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
        dto.setParticipantName(attempt.getParticipant().getFirstName());
        dto.setImageUrl(attempt.getParticipant().getImageUrl());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        return dto;
    }

    private ParticipantScoreDTO convertToParticipantScoreDTO(QuizAttempt attempt) {
        ParticipantScoreDTO dto = new ParticipantScoreDTO();
        dto.setParticipantName(attempt.getParticipant().getFirstName());
        dto.setImageUrl(attempt.getParticipant().getImageUrl());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setTotalQuestions(attempt.getTotalQuestions());

        List<QuestionScoreDTO> questionScores = quizAnswerRepository
                .findByParticipantAndQuestionQuiz(attempt.getParticipant(), attempt.getQuiz())
                .stream()
                .map(this::convertToQuestionScoreDTO)
                .collect(Collectors.toList());
        dto.setQuestionScores(questionScores);

        return dto;
    }

    private QuestionScoreDTO convertToQuestionScoreDTO(QuizAnswer answer) {
        QuestionScoreDTO dto = new QuestionScoreDTO();
        dto.setQuestionText(answer.getQuestion().getQuestionText());
        dto.setSelectedOption(answer.getSelectedOption());
        dto.setCorrectAnswer(answer.getQuestion().getCorrectAnswer());
        dto.setCorrect(answer.isCorrect());
        return dto;
    }


    private QuizDTO convertToQuizDTO(Quiz quiz) {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(String.valueOf(quiz.getId()));
        quizDTO.setTitle(quiz.getTitle());
        quizDTO.setDescription(quiz.getDescription());
        quizDTO.setStartTime(quiz.getStartTime());
        quizDTO.setEndTime(quiz.getEndTime());
        return quizDTO;
    }

    private QuestionDTO convertToQuestionDTO(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(String.valueOf(question.getId()));
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setOptions(question.getOptions());
        return questionDTO;
    }

    private QuizAttemptDTO convertToQuizAttemptDTO(QuizAttempt attempt) {
        QuizAttemptDTO attemptDTO = new QuizAttemptDTO();
        attemptDTO.setQuizId(String.valueOf(attempt.getQuiz().getId()));
        attemptDTO.setTitle(attempt.getQuiz().getTitle());
        attemptDTO.setTotalQuestions(attempt.getTotalQuestions());
        attemptDTO.setCorrectAnswers(attempt.getCorrectAnswers());
        attemptDTO.setAttemptTime(attempt.getAttemptTime());
        return attemptDTO;
    }
}
