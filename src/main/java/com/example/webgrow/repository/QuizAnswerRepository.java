package com.example.webgrow.repository;

import com.example.webgrow.models.QuizAnswer;
import com.example.webgrow.models.User;
import com.example.webgrow.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByParticipantAndQuestionQuiz(User participant, Quiz quiz);

    Long countByParticipantAndQuestionQuiz(User user,Quiz quiz);

}
