package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.QuizHostService;
import com.example.webgrow.models.Question;
import com.example.webgrow.models.Quiz;
import com.example.webgrow.payload.dto.QuestionDTO;
import com.example.webgrow.repository.QuestionRepository;
import com.example.webgrow.repository.QuizRepository;
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

    @Override
    public void addQuestionsToQuiz(List<QuestionDTO> questionDTOList) {
        if (questionDTOList.isEmpty()) {
            throw new RuntimeException("No questions provided!");
        }

        // Retrieve the Quiz ID from the first question
        Long quizId = questionDTOList.get(0).getQuizId();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

        if (questionDTOList.stream().anyMatch(q -> !q.getId().equals(quizId))) {
            throw new RuntimeException("All questions must belong to the same quiz!");
        }

        AtomicInteger nextQuestionNumber = new AtomicInteger((int)questionRepository.countByQuiz(quiz) + 1);

        List<Question> questions = questionDTOList.stream().map(questionDTO -> {
            Question question = new Question();
            question.setQuiz(quiz);
            question.setQuestionText(questionDTO.getQuestionText());
            question.setOptions(questionDTO.getOptions());

            if (!questionDTO.getOptions().contains(questionDTO.getCorrectAnswer())) {
                throw new RuntimeException("Correct answer must be one of the options!");
            }

            question.setCorrectAnswer(questionDTO.getCorrectAnswer());

            question.setQuestionNumber(nextQuestionNumber.getAndIncrement());
            return question;
        }).collect(Collectors.toList());

        questionRepository.saveAll(questions);
    }
}
