package com.example.webgrow.repository;

import com.example.webgrow.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.webgrow.models.Event;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.host.role = 'HOST'")
    List<Event> findEvents();

    Page<Event> findByHostEmail(String hostEmail, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "(e.registerStart <= CURRENT_TIMESTAMP AND e.registerEnd >= CURRENT_TIMESTAMP) " +
            "OR (e.registerStart > CURRENT_TIMESTAMP)")
    Page<Event> findOngoingOrUpcomingRegistrations(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.startTime > :currentTime")
    List<Event> findUpcomingEvents(@Param("currentTime") LocalDateTime currentTime);
}
