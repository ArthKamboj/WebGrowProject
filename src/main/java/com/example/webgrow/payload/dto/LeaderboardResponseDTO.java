package com.example.webgrow.payload.dto;

import lombok.Data;

import java.util.List;

@Data
public class LeaderboardResponseDTO {
    private List<LeaderboardEntryDTO> topScores;
    private ParticipantScoreDTO participantDetails;
}
