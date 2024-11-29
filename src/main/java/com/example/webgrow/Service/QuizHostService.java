package com.example.webgrow.Service;

import com.example.webgrow.payload.dto.QuestionDTO;
import com.example.webgrow.payload.dto.QuizAttemptDTO;
//import com.example.webgrow.repository.QuizRepository;

import java.util.List;

public interface QuizHostService {

//    private final QuizRepository quizRepository;

    void addQuestionsToQuiz(Long quizId, List<QuestionDTO> questionDTOList);

    List<QuizAttemptDTO> getQuizAttempts(Long quizId);

}
