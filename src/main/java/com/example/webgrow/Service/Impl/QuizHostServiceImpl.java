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

}
