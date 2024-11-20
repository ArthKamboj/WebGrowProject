package com.example.webgrow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.webgrow.models.Event;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
            "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:category IS NULL OR e.category = :category) AND " +
            "(:location IS NULL OR e.location = :location)")
    List<Event> findEvents(@Param("search") String search,
                           @Param("category") String category,
                           @Param("location") String location);

    List<Event> findByHostId(Long hostId);

    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :participantId")
    List<Event> findByParticipantId(@Param("participantId") Long participantId);

}
