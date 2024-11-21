package com.example.webgrow.repository;

import com.example.webgrow.models.Question;
import com.example.webgrow.models.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByQuiz(Quiz quiz, Pageable pageable);
    long countByQuiz(Quiz quiz);
}
