package com.example.webgrow.repository;

import com.example.webgrow.models.Notification;
import com.example.webgrow.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByParticipantId(Long participantId, Pageable pageable);

    List<Notification> findByParticipantId(User participant);
}
