package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.dto.NotificationDTO;
import com.example.webgrow.payload.dto.UserDTO;
import com.example.webgrow.repository.*;
import jakarta.transaction.Transactional;
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // 1. Get All Events
    @Override
    public List<EventDTO> getAllEvents(String search, String category, String location) {
        return eventRepository.findEvents(search, category, location)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Get Registered Events
    @Override
    public List<EventDTO> getRegisteredEvents(String email) {
        User user = getUserByEmail(email);
        return registrationRepository.findByParticipantId(user.getId())
                .stream()
                .map(registration -> convertToDTO(registration.getEvent()))
                .collect(Collectors.toList());
    }

    // 3. Register for an Event
    @Override
    @Transactional
    public String registerForEvent(String email, Long eventId) {
        User user = getUserByEmail(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = new Registration(user, event);
        registrationRepository.save(registration);

        return "Successfully registered for the event";
    }

    // 4. Add to Favourites
    @Override
    @Transactional
    public String addToFavourites(String email, Long eventId) {
        User user = getUserByEmail(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Favourite favourite = new Favourite(null, user, event);
        favouriteRepository.save(favourite);

        return "Event added to favourites";
    }

    // 5. Get Favourite Events
    @Override
    public List<EventDTO> getFavouriteEvents(String email) {
        User user = getUserByEmail(email);
        return favouriteRepository.findByParticipantId(user.getId())
                .stream()
                .map(favourite -> convertToDTO(favourite.getEvent()))
                .collect(Collectors.toList());
    }

    // 6. Unregister from an Event
    @Override
    @Transactional
    public String unregisterFromEvent(String email, Long eventId) {
        User user = getUserByEmail(email);
        Registration registration = registrationRepository
                .findByParticipantIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registrationRepository.delete(registration);

        return "Successfully unregistered from the event";
    }

    // 7. Unmark an Event as Favourite
    @Override
    @Transactional
    public String unmarkAsFavourite(String email, Long eventId) {
        User user = getUserByEmail(email);
        Favourite favourite = favouriteRepository
                .findByParticipantIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new RuntimeException("Favourite not found"));

        favouriteRepository.delete(favourite);

        return "Event successfully removed from favourites";
    }

    // 8. Get Participant Profile
    @Override
    public UserDTO getParticipantProfile(String email) {
        User user = getUserByEmail(email);
        return UserDTO.from(user);
    }

    // 9. Update Participant Profile
    @Override
    @Transactional
    public void updateParticipantProfile(String email, User updatedProfile) {
        User user = getUserByEmail(email);

        if (updatedProfile.getFirstName() != null) {
            user.setFirstName(updatedProfile.getFirstName());
        }
        if (updatedProfile.getLastName() != null) {
            user.setLastName(updatedProfile.getLastName());
        }
        if (updatedProfile.getMobile() != null) {
            user.setMobile(updatedProfile.getMobile());
        }
        System.out.println("Updated user details: " + user);


        userRepository.save(user);
    }

    // 10. Get Notifications
    @Override
    public List<NotificationDTO> getNotifications(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.findByParticipantId(user.getId())
                .stream()
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
