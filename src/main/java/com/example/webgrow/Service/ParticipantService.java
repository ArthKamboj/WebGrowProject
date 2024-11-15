package com.example.webgrow.Service;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.UserDTO;

import java.util.List;

public interface ParticipantService {

    public List<EventDTO> getAllEvents(String search, String category, String location);

    public String registerForEvent(Integer participantId, Long eventId);

    public List<EventDTO> getRegisteredEvents(Integer participantId);

    public String addToFavourites(Integer participantId, Long eventId);

    public List<EventDTO> getFavouriteEvents(Integer participantId);

    public String unregisterFromEvent(Integer participantId, Long eventId);

    public String unmarkAsFavourite(Integer participantId, Long eventId);

    public User getParticipantProfile(Integer participantId);

    public void updateParticipantProfile(Integer participantId, User updatedProfile);

}
