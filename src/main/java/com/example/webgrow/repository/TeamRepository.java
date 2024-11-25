package com.example.webgrow.repository;

import com.example.webgrow.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByEventIdAndIsPublicTrue(Long eventId);
    List<Team> findByEventIdAndIsPublicTrueAndNameContainingIgnoreCase(Long eventId, String name);
    List<Team> findByEventIdAndMembers_Id(Long eventId, Long memberId);
}
