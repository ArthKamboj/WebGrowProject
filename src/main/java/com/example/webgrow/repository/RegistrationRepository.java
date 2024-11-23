package com.example.webgrow.repository;


import com.example.webgrow.models.Registration;
import com.example.webgrow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<User> findByEventId(Long eventId);
    List<Registration> findByParticipantId(Long Id);
    Optional<Registration> findByParticipantIdAndEventId(Long Id, Long EventId);
}
