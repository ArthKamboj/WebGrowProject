package com.example.webgrow.repository;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.models.UserEventView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserEventViewRepository extends JpaRepository<UserEventView, Long> {

    // Find views by user, event, and timestamp
    Optional<UserEventView> findByUserAndEventAndViewedAtAfter(User user, Event event, LocalDateTime oneHourAgo);

    // Find recently viewed events by user
    @Query("SELECT uev.event FROM UserEventView uev " +
            "WHERE uev.user.email = :email " +
            "AND uev.viewedAt >= :oneHourAgo")
    Page<Event> findRecentEventViewsByUser(
            @Param("email") String email,
            @Param("oneHourAgo") LocalDateTime oneHourAgo,
            Pageable pageable);
}