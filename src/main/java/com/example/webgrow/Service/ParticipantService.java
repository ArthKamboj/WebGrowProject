package com.example.webgrow.Service;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.LeaderboardResponseDTO;
import com.example.webgrow.payload.dto.NotificationDTO;
import com.example.webgrow.payload.dto.UserDTO;

import java.util.List;

public interface ParticipantService {

    public List<EventDTO> getAllEvents(String search, String category, String location);

    public String registerForEvent(String email, Long eventId);

    public List<EventDTO> getRegisteredEvents(String email);

    public String addToFavourites(String email, Long eventId);

    public List<EventDTO> getFavouriteEvents(String email);

    public String unregisterFromEvent(String email, Long eventId);

    public String unmarkAsFavourite(String email, Long eventId);

    public UserDTO getParticipantProfile(String email);

    EventDTO getEventDetails(Long eventId);

    public void updateParticipantProfile(String email, User updatedProfile);

    public List<NotificationDTO> getNotifications(String email, int page, int size);
}
