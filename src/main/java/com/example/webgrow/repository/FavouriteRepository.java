package com.example.webgrow.repository;


import com.example.webgrow.models.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    List<Favourite> findByParticipantId(Integer participantId);
    Optional<Favourite> findByParticipantIdAndEventId(Integer participantId, Long eventId);

}
