package com.example.webgrow.repository;
import com.example.webgrow.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByHostId(Integer hostId);
}
