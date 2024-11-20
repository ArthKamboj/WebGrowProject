package com.example.webgrow.repository;

import com.example.webgrow.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByParticipantId(Long participantId, Pageable pageable);
}
