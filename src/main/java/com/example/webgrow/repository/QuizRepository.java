package com.example.webgrow.repository;

import com.example.webgrow.models.Quiz;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.QuizAttemptDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByParticipantsContaining(User participant);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    List<QuizAttemptDTO> getQuizAttempts(@Param("quizId") Long quizId);

    List<Quiz> findByCategory(String category);

    List<Quiz> findByEndTimeBeforeAndCompletedFalse(LocalDateTime now);

}
