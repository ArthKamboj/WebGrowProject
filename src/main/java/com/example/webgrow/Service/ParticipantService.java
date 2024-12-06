package com.example.webgrow.Service;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ParticipantService {

    ApiResponse<List<EventDTO>> getAllEvents();

    ApiResponse<String> registerForEvent(String email, Long eventId);

    ApiResponse<List<EventDTO>> getRegisteredEvents(String email);

    ApiResponse<String> addToFavourites(String email, Long eventId);

    ApiResponse<List<EventDTO>> getFavouriteEvents(String email);

    ApiResponse<String> unregisterFromEvent(String email, Long eventId);

    ApiResponse<String> unmarkAsFavourite(String email, Long eventId);

    ApiResponse<UserDTO> getParticipantProfile(String email);

    ApiResponse<EventDTO> getEventDetails(Long eventId, String email);

    ApiResponse<EventDTO> getEventDetails(Long eventId);

    ApiResponse<TeamDTO> getTeam(String email, Long eventId);

    ApiResponse<List<NotificationDTO>> getNotifications(String email, int page, int size);

    ApiResponse<String> createTeam(String email, Long eventId, TeamRequest teamRequest);

    ApiResponse<List<TeamResponse>> searchTeams(Long eventId, String teamName);

    ApiResponse<String> requestToJoinTeam(String email, Long teamId);

    ApiResponse<String> respondToJoinRequest(Long requestId, String response);

    Page<EventDTO> getRecentlyViewedEvents(String email, int page, int size);

    List<EventDTO> getPastRegisteredEvents(String email);

}
