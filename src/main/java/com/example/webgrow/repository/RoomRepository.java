package com.example.webgrow.repository;

import com.example.webgrow.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByEventId(Long eventId);
    Optional<Room> findByIdAndEventId(Long id, Long eventId);
}
