package com.example.webgrow.repository;

import com.example.webgrow.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByEventIdAndIsPublicTrue(Long eventId);
    List<Team> findByEventIdAndIsPublicTrueAndNameContainingIgnoreCase(Long eventId, String name);
    List<Team> findByEventIdAndMembers_Id(Long eventId, Long memberId);
//    Team findById(Long teamId);
}
