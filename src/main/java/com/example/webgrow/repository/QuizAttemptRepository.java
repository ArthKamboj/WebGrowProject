//package com.example.webgrow.repository;
//
//import com.example.webgrow.models.QuizAttempt;
//import com.example.webgrow.models.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
//
//    Optional<QuizAttempt> findByParticipantAndQuiz(User participant, Long quizId);
//
//}