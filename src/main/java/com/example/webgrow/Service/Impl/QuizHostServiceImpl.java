package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.QuizHostService;
import com.example.webgrow.models.Question;
import com.example.webgrow.models.Quiz;
import com.example.webgrow.models.QuizAttempt;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.QuestionDTO;
import com.example.webgrow.payload.dto.QuizAttemptDTO;
import com.example.webgrow.repository.QuestionRepository;
import com.example.webgrow.repository.QuizAttemptRepository;
import com.example.webgrow.repository.QuizRepository;
import com.example.webgrow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizHostServiceImpl implements QuizHostService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    @Override
    public void addQuestionsToQuiz(Long quizId, List<QuestionDTO> questionDTOList) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found!"));

        List<Question> questions = questionDTOList.stream()
                .map(dto -> {
                    Question question = new Question();
                    question.setQuestionText(dto.getQuestionText());
                    question.setOptions(dto.getOptions());
                    question.setCorrectAnswer(dto.getCorrectAnswer());
                    question.setQuiz(quiz);
                    return question;
                }).collect(Collectors.toList());

        questionRepository.saveAll(questions);
    }

    @Override
    public List<QuizAttemptDTO> getQuizAttempts(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found!"));

        List<QuizAttempt> attempts = quizAttemptRepository.findByQuiz(quiz);

        return attempts.stream().map(attempt -> {
            QuizAttemptDTO dto = new QuizAttemptDTO();
            User participant = attempt.getParticipant();
            dto.setParticipantId(participant.getId());
            dto.setParticipantName(participant.getFirstName() + " " + participant.getLastName());
            dto.setCorrectAnswers(attempt.getCorrectAnswers());
            dto.setTotalQuestions(attempt.getTotalQuestions());
            dto.setAttemptTime(attempt.getAttemptTime());
            return dto;
        }).collect(Collectors.toList());
    }

}
