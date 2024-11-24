package com.example.webgrow.repository;


import com.example.webgrow.models.Favourite;
import com.example.webgrow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    @Query("SELECT f.participant FROM Favourite f WHERE f.event.id = :eventId")
    List<User> findByEventId(@Param("eventId") Long eventId);
    List<Favourite> findByParticipantId(Long participantId);
    Optional<Favourite> findByParticipantIdAndEventId(Long participantId, Long eventId);

}
