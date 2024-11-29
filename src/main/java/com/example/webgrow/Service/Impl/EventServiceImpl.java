package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.dto.EventDTO;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.response.EventResponse;
import com.example.webgrow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final FavouriteRepository favouriteRepository;
    private final NotificationRepository notificationRepository;
    private final RoomRepository roomRepository;
//    private final WebinarRepository webinarRepository;

    @Override
    public DTOClass createEvent(EventRequest eventRequest, String email) {
        User host = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Host with email " + email + " not found"));

        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartTime(eventRequest.getStartTime());
        event.setCategory(eventRequest.getCategory());
        event.setEndTime(eventRequest.getEndTime());
        event.setHost(host);
        event.setOrganization(eventRequest.getOrganization());
        event.setEventType(eventRequest.getEventType());
        event.setImageUrl(eventRequest.getImageUrl());
        event.setMode(eventRequest.getMode());
        event.setRegisterStart(eventRequest.getRegisterStart());
        event.setRegisterEnd(eventRequest.getRegisterEnd());
        event.setFestival(eventRequest.getFestival());
        event.setCapacityMin(eventRequest.getCapacityMin());
        event.setCapacityMax(eventRequest.getCapacityMax());
        event.setUrl(eventRequest.getUrl());
        event.setTeamCreationAllowed(eventRequest.isTeamCreationAllowed());
        if (event.isTeamCreationAllowed()) {
            event.setMinTeamSize(eventRequest.getMinTeamSize());
            event.setMaxTeamSize(eventRequest.getMaxTeamSize());
        }
        event.setLastUpdate(LocalDateTime.now());
        eventRepository.save(event);

        // If it's a quiz event, create a corresponding Quiz
        if (eventRequest.getCategory().toLowerCase().contains("quiz")) {
            Quiz quiz = new Quiz();
            quiz.setTitle(eventRequest.getTitle());
            quiz.setDescription(eventRequest.getDescription());
            quiz.setHost(host);
            quiz.setStartTime(eventRequest.getStartTime());
            quiz.setEndTime(eventRequest.getEndTime());
            quiz.setEventType(eventRequest.getEventType());
            quiz.setCategory(eventRequest.getCategory());
            quiz.setRegisterStart(eventRequest.getRegisterStart());
            quiz.setRegisterEnd(eventRequest.getRegisterEnd());
            quiz.setFestival(eventRequest.getFestival());
            quiz.setOrganization(eventRequest.getOrganization());
            quiz.setCapacityMin(eventRequest.getCapacityMin());
            quiz.setCapacityMax(eventRequest.getCapacityMax());
            quiz.setParticipants(event.getParticipants());
            quiz.setIsActive(true);

            quizRepository.save(quiz);
        }
//        if (eventRequest.getCategory().toLowerCase().contains("webinar")) {
//            Webinar webinar = new Webinar();
//            webinar.setTitle(eventRequest.getTitle());
//            webinar.setDescription(eventRequest.getDescription());
//            webinar.setHost(host);
//            webinar.setStartTime(eventRequest.getStartTime());
//            webinar.setEndTime(eventRequest.getEndTime());
//            webinar.setEventType(eventRequest.getEventType());
//            webinar.setCategory(eventRequest.getCategory());
//            webinar.setRegisterStart(eventRequest.getRegisterStart());
//            webinar.setRegisterEnd(eventRequest.getRegisterEnd());
//            webinar.setFestival(eventRequest.getFestival());
//            webinar.setCapacityMin(eventRequest.getCapacityMin());
//            webinar.setCapacityMax(eventRequest.getCapacityMax());
//            webinar.setParticipants(event.getParticipants());
//            webinar.setIsActive(true);
//
//            webinarRepository.save(webinar);
//        }

        return new DTOClass("Event Created Successfully", "SUCCESS", null);
    }

    @Override
    @Transactional
    public DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event not found with ID: " + eventId));
        if (!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Host Email Not Matched", "FAILURE", null);
        }
        boolean isAuthorized = event.getHost().getEmail().equals(hostEmail) ||
                event.getAdministrators().stream().anyMatch(admin -> admin.getEmail().equals(hostEmail));

        if (!isAuthorized) {
            return new DTOClass("Unauthorized to update the event", "FAILURE", null);
        }

        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setFestival(eventRequest.getFestival());
        event.setOrganization(eventRequest.getOrganization());
        event.setCapacityMin(eventRequest.getCapacityMin());
        event.setCapacityMax(eventRequest.getCapacityMax());
        event.setRegisterEnd(eventRequest.getRegisterEnd());
        event.setUrl(eventRequest.getUrl());
        event.setTeamCreationAllowed(eventRequest.isTeamCreationAllowed());
        event.setMinTeamSize(eventRequest.getMinTeamSize());
        event.setMaxTeamSize(eventRequest.getMaxTeamSize());
        event.setLastUpdate(LocalDateTime.now());

        sendEventUpdateNotifications(event);
        if (eventRequest.getRegisterStart() != null && !eventRequest.getRegisterStart().isBefore(LocalDateTime.now())) {
            event.setRegisterStart(eventRequest.getRegisterStart());
        } else {
            return new DTOClass("Registration already started", "ERROR", null);
        }

        eventRepository.save(event);
        return new DTOClass("Event Updated Successfully", "SUCCESS", null);
    }

    private void sendEventUpdateNotifications(Event event) {
        List<User> registeredUsers = registrationRepository.findByEventId(event.getId());
        List<User> favoriteUsers = favouriteRepository.findByEventId(event.getId());

        for (User user : registeredUsers) {
            createNotification(user, event, "The event you registered for has been updated.");
        }

        for (User user : favoriteUsers) {
            createNotification(user, event, "The event you favorited has been updated.");
        }
    }

    private void createNotification(User user, Event event, String message) {
        Notification notification = Notification.builder()
                .participant(user)
                .message(message)
                .timestamp(LocalDateTime.now())
                .read(false)
                .event(event)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public DTOClass deleteEvent(Long eventId, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if (!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Unauthorized to delete this event", "FAILURE", null);
        }
        eventRepository.delete(event);
        return new DTOClass("Event Deleted Successfully", "SUCCESS", null);
    }

    @Override
    public Page<DTOClass> getEventList(String hostEmail, Pageable pageable) {
        User host = userRepository.findByEmail(hostEmail)
                .orElseThrow(() -> new RuntimeException("Host not found"));

        Page<Event> eventsPage = eventRepository.findByHostEmail(host.getEmail(), pageable);

        Page<DTOClass> dtoPage = eventsPage.map(event -> {
            EventResponse eventResponse = new EventResponse();
            eventResponse.setId(String.valueOf(event.getId()));
            eventResponse.setTitle(event.getTitle());
            eventResponse.setDescription(event.getDescription());
            eventResponse.setLocation(event.getLocation());
            eventResponse.setMode(event.getMode());
            eventResponse.setFestival(event.getFestival());
            eventResponse.setEventType(event.getEventType());
            eventResponse.setOrganization(event.getOrganization());
            eventResponse.setCapacityMin(event.getCapacityMin());
            eventResponse.setCapacityMax(event.getCapacityMax());
            eventResponse.setStartTime(event.getStartTime());
            eventResponse.setEndTime(event.getEndTime());
            eventResponse.setRegisterStart(event.getRegisterStart());
            eventResponse.setRegisterEnd(event.getRegisterEnd());
            eventResponse.setImageUrl(event.getImageUrl());
            eventResponse.setHostEmail(event.getHost().getEmail());
            eventResponse.setLastUpdate(LocalDateTime.now());

            return new DTOClass("Event Retrieved Successfully", "SUCCESS", eventResponse); // Assuming DTOClass can handle single event
        });

        return dtoPage;
    }

    @Override
    public DTOClass getEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        EventResponse response = new EventResponse();
        response.setId(String.valueOf(event.getId()));
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setLocation(event.getLocation());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setOrganization(event.getOrganization());
        response.setMode(event.getMode());
        response.setFestival(event.getFestival());
        response.setEventType(event.getEventType());
        response.setRegisterStart(event.getRegisterStart());
        response.setRegisterEnd(event.getRegisterEnd());
        response.setCapacityMin(event.getCapacityMin());
        response.setCapacityMax(event.getCapacityMax());
        response.setHostEmail(event.getHost().getEmail());
        return new DTOClass("Event details retrieved successfully", "SUCCESS", response);
    }

    public DTOClass createRooms(Long eventId, int roomCount, List<String> roomNames) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        for (int i = 0; i < roomCount; i++) {
            String roomName = roomNames != null && i < roomNames.size()
                    ? roomNames.get(i)
                    : "room-" + (i + 1);
            Room room = new Room();
            room.setName(roomName);
            room.setEvent(event);
            room.setVacant(true);
            room.setLastUpdated(LocalDateTime.now());
            roomRepository.save(room);
        }
        return new DTOClass("Rooms created successfully", "SUCCESS", null);
    }

    @Override
    public DTOClass getParticipants(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + eventId));

        List<User> participants = registrationRepository.findByEventId(eventId);

        if (participants.isEmpty()) {
            return new DTOClass("No participants registered for this event", "SUCCESS", null);
        }

        List<String> participantDetails = participants.stream()
                .map(user -> "Name: " + user.getFirstName() + ", Email: " + user.getEmail())
                .collect(Collectors.toList());

        return new DTOClass("Participants retrieved successfully", "SUCCESS", participantDetails);
    }

    @Override
    public DTOClass assignAdministrators(Long eventId, Long hostId, String hostEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        if (!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Unauthorized to assign administrators", "FAILURE", null);
        }
        User adminUser = userRepository.findById(hostId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + hostId));
        if (event.getAdministrators().contains(adminUser)) {
            return new DTOClass("User is already an administrator", "FAILURE", null);
        }
        event.getAdministrators().add(adminUser);
        eventRepository.save(event);
        return new DTOClass("Administrators assigned successfully", "SUCCESS", null);
    }

    @Override
    public List<User> getAdministrators(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return event.getAdministrators();
    }

    // Method to update room status
    public DTOClass updateRoomStatus(Long roomId, boolean isVacant) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setVacant(isVacant);
        room.setLastUpdated(LocalDateTime.now());
        roomRepository.save(room);
        return new DTOClass("Room updated successfully", "SUCCESS", null);
    }

    // Method to fetch rooms for an event
    public List<Room> getRoomsForEvent(Long eventId) {
        return roomRepository.findByEventId(eventId);
    }

    public Page<EventDTO> getUnloggedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdate"));
        Page<Event> events = eventRepository.findOngoingOrUpcomingRegistrations(pageable);
        return events.map(this::convertToEventDTO);
    }

    private EventDTO convertToEventDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(String.valueOf(event.getId()));
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setCategory(event.getCategory());
        dto.setCapacityMin(event.getCapacityMin());
        dto.setCapacityMax(event.getCapacityMax());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setRegisterStart(event.getRegisterStart());
        dto.setRegisterEnd(event.getRegisterEnd());
        dto.setMode(event.getMode());
        dto.setLastUpdate(event.getLastUpdate());
        dto.setImageUrl(event.getImageUrl());
        dto.setActive(event.isActive());
        return dto;
    }
}
