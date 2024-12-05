package com.example.webgrow.repository;

import com.example.webgrow.models.Role;
import com.example.webgrow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByVerifiedFalseAndGeneratedAtBefore(LocalDateTime expiryTime);
    List<User> findByRole(Role role);
}