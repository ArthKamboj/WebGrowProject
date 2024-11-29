package com.example.webgrow.repository;

import com.example.webgrow.models.TimeLineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineEntryRepository extends JpaRepository<TimeLineEntry, Long> {
}
