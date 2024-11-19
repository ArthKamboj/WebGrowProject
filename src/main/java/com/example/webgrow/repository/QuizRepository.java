package com.example.webgrow.repository;

import com.example.webgrow.models.Quiz;
import com.example.webgrow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByParticipantsContaining(User participant);
}
