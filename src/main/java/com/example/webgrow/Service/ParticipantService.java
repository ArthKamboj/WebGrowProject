package com.example.webgrow.Service;

import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.UserDTO;
import jdk.jfr.Event;

import java.util.List;

public interface ParticipantService {

    public List<EventDTO> getAllEvents(String search, String category, String location);

    public void registerForEvent(Long eventId, Integer Id);

    public List<EventDTO> getRegisteredEvents(Integer Id);

    public void markEventAsFavorite(Long eventId, Integer Id);

    public void unmarkEventAsFavorite(Long eventId, Integer Id);

    public UserDTO getProfile(Integer Id);

//    public void updateProfile(Integer Id, UserDTO userDTO);
//
//    private EventDTO convertToDTO(Event event) { return null; }

    private UserDTO convertToUserDTO(User user) { return null; }
}
