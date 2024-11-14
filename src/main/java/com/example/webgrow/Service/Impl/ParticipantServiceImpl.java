package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.Event;
import com.example.webgrow.models.Favourite;
import com.example.webgrow.models.Registration;
import com.example.webgrow.models.User;
import com.example.webgrow.models.Registration;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.UserDTO;
import com.example.webgrow.repository.EventRepository;
import com.example.webgrow.repository.FavouriteRepository;
import com.example.webgrow.repository.RegistrationRepository;
import com.example.webgrow.repository.UserRepository;
import com.example.webgrow.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final FavouriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public List<EventDTO> getAllEvents(String search, String category, String location) {
        return eventRepository.findEvents(search, category, location)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private EventDTO convertToDTO(Event event) {
        return new EventDTO(event.getId(), event.getTitle(), event.getDescription(),
                event.getDate(), event.getLocation(), event.getCategory());
    }

    public void registerForEvent(Long eventId, Integer Id) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Registration registration = new Registration(Id, eventId);
        registrationRepository.save(registration);
    }

    public List<EventDTO> getRegisteredEvents(Integer Id) {
        return registrationRepository.findByParticipantId(Id)
                .stream()
                .map(registration -> convertToDTO(registration.getEvent()))
                .collect(Collectors.toList());
    }

    public void markEventAsFavorite(Long eventId, Integer Id) {
        Favourite favorite = new Favourite(Id, eventId);
        favoriteRepository.save(favorite);
    }

    public void unmarkEventAsFavorite(Long eventId, Integer Id) {
        favoriteRepository.deleteByIdAndEventId(Id, eventId);
    }

    public UserDTO getProfile(Integer Id) {
        return userRepository.findById(Id)
                .map(this::convertToUserDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    public void updateProfile(Integer Id, UserDTO userDTO) {
//        User user = userRepository.findById(Id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setFirstName(userDTO.getName());
//        user.setEmail(userDTO.getEmail());
//        userRepository.save(user);
//    }
//
//    private EventDTO convertToDTO(Event event) {
//        return new EventDTO(event.getId(), event.getTitle(), event.getDescription(),
//                event.getDate(), event.getLocation(), event.getCategory());
//    }

    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail());
    }
}
