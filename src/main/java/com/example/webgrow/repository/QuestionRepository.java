package com.example.webgrow.repository;

import com.example.webgrow.models.Question;
import com.example.webgrow.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuiz(Quiz quiz);
    Optional<Question> findByQuizAndQuestionNumber(Quiz quiz, int questionNumber);
    long countByQuiz(Quiz quiz);
}
