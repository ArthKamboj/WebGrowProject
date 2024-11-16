package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.models.Registration;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.NotificationDTO;
import com.example.webgrow.payload.dto.UserDTO;
import com.example.webgrow.repository.*;
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
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;



    // 1. Get All Events
    public List<EventDTO> getAllEvents(String search, String category, String location) {
        return eventRepository.findEvents(search, category, location)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Get Registered Events
    public List<EventDTO> getRegisteredEvents(Integer participantId) {
        return registrationRepository.findByParticipantId(participantId)
                .stream()
                .map(registration -> convertToDTO(registration.getEvent()))
                .collect(Collectors.toList());
    }

    // 3. Register for an Event
    public String registerForEvent(Integer participantId, Long eventId) {
        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = new Registration(participant, event);
        registrationRepository.save(registration);

        return "Successfully registered for the event";
    }

    // 4. Add to Favourites
    public String addToFavourites(Integer participantId, Long eventId) {
        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Favourite favourite = new Favourite(null, participant, event);
        favouriteRepository.save(favourite);

        return "Event added to favourites";
    }

    // 5. Get Favourite Events
    public List<EventDTO> getFavouriteEvents(Integer participantId) {
        return favouriteRepository.findByParticipantId(participantId)
                .stream()
                .map(favourite -> convertToDTO(favourite.getEvent()))
                .collect(Collectors.toList());
    }

    // 6. Unregister from an Event
    public String unregisterFromEvent(Integer participantId, Long eventId) {
        Registration registration = registrationRepository
                .findByParticipantIdAndEventId(participantId, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registrationRepository.delete(registration);

        return "Successfully unregistered from the event";
    }

    // 7. Unmark an Event as Favourite
    public String unmarkAsFavourite(Integer participantId, Long eventId) {
        Favourite favourite = favouriteRepository
                .findByParticipantIdAndEventId(participantId, eventId)
                .orElseThrow(() -> new RuntimeException("Favourite not found"));

        favouriteRepository.delete(favourite);

        return "Event successfully removed from favourites";
    }

    // 8. Get Participant Profile
    public User getParticipantProfile(Integer participantId) {
        return userRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));
    }

    // 9. Update Participant Profile
    public void updateParticipantProfile(Integer participantId, User updatedProfile) {
        User existingUser = userRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        // Update fields
        if (updatedProfile.getFirstName() != null) {
            existingUser.setFirstName(updatedProfile.getFirstName());
        }
        if (updatedProfile.getLastName() != null) {
            existingUser.setLastName(updatedProfile.getLastName());
        }
        if (updatedProfile.getEmail() != null) {
            existingUser.setEmail(updatedProfile.getEmail());
        }
        if (updatedProfile.getMobile() != null) {
            existingUser.setMobile(updatedProfile.getMobile());
        }
        if (updatedProfile.getDesignation() != null) {
            existingUser.setDesignation(updatedProfile.getDesignation());
        }
        if (updatedProfile.getOrganization() != null) {
            existingUser.setOrganization(updatedProfile.getOrganization());
        }

        userRepository.save(existingUser);
    }

    public List<NotificationDTO> getNotifications(Integer participantId) {
        List<Notification> notifications = notificationRepository.findByParticipantId(participantId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .participant_id(notification.getParticipant().getId())
                .message(notification.getMessage())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .build();
    }

    // Helper Method to Convert Event to EventDTO
    private EventDTO convertToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .category(event.getCategory())
                .capacity(event.getCapacity())
                .build();
    }
}