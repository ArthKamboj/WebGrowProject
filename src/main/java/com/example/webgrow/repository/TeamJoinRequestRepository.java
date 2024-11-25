package com.example.webgrow.repository;

import com.example.webgrow.models.TeamJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    List<TeamJoinRequest> findByTeam_IdAndStatus(Long teamId, String status);
}
