package com.example.webgrow.Service;

import com.example.webgrow.payload.dto.QuestionDTO;

import java.util.List;

public interface QuizHostService {

    public void addQuestionsToQuiz(List<QuestionDTO> questionDTOList);


}
