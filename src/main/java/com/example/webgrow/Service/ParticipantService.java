package com.example.webgrow.Service;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ParticipantService {

    public ApiResponse<List<EventDTO>> getAllEvents();

    public ApiResponse<String> registerForEvent(String email, Long eventId);

    public ApiResponse<List<EventDTO>> getRegisteredEvents(String email);

    public ApiResponse<String> addToFavourites(String email, Long eventId);

    public ApiResponse<List<EventDTO>> getFavouriteEvents(String email);

    public ApiResponse<String> unregisterFromEvent(String email, Long eventId);

    public ApiResponse<String> unmarkAsFavourite(String email, Long eventId);

    public ApiResponse<UserDTO> getParticipantProfile(String email);

    ApiResponse<EventDTO> getEventDetails(Long eventId, String email);

    public ApiResponse<String> updateParticipantProfile(String email, User updatedProfile);

    public ApiResponse<List<NotificationDTO>> getNotifications(String email, int page, int size);

    public ApiResponse<String> createTeam(String email, Long eventId, TeamRequest teamRequest);

    public ApiResponse<List<TeamResponse>> searchTeams(Long eventId, String teamName);

    public ApiResponse<String> requestToJoinTeam(String email, Long teamId);

    public ApiResponse<String> respondToJoinRequest(Long requestId, String response);

    Page<EventDTO> getRecentlyViewedEvents(String email, int page, int size);

    List<EventDTO> getPastRegisteredEvents(String email);

}
