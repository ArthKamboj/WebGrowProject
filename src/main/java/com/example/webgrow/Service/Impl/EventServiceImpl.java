package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.dto.*;
import com.example.webgrow.payload.request.BulkTimelineEntryRequest;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.request.UpdateProfileRequest;
import com.example.webgrow.payload.response.EventResponse;
import com.example.webgrow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final FavouriteRepository favouriteRepository;
    private final NotificationRepository notificationRepository;
    private final RoomRepository roomRepository;
    private final TimelineEntryRepository timelineEntryRepository;

    private User getHostByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

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
            if(eventRequest.getMinTeamSize() > eventRequest.getMaxTeamSize())
            {
                throw new RuntimeException("Maximum team size cannot be less that minimum team size");
            }
        }
        event.setLastUpdate(LocalDateTime.now());
        if(eventRequest.getRegisterEnd().isBefore(eventRequest.getRegisterStart()))
        {
            throw new RuntimeException("Registration cannot end before starting");
        }
        if(eventRequest.getCapacityMax() < eventRequest.getCapacityMin())
        {
            throw new RuntimeException("Maximum capacity cannot be less than minimum capacity");
        }
        if(eventRequest.getEndTime().isBefore(eventRequest.getStartTime()))
        {
            throw new RuntimeException("End time cannot be before start time");
        }
        if(eventRequest.getStartTime().isBefore(eventRequest.getRegisterStart()))
        {
            throw new RuntimeException("Registrations must start before event");
        }
        eventRepository.save(event);

        if (eventRequest.getCategory().toLowerCase().contains("quiz")) {
            Quiz quiz = new Quiz();
            quiz.setId(event.getId());
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

        return new DTOClass("Event Created Successfully", "SUCCESS", null);
    }

    @Override
    public DTOClass updateUserDetails(UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =authentication.getName();
        var user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found with email: " + email));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobile(request.getMobile());
        user.setImageUrl(request.getImageUrl());
        user.setDesignation(request.getDesignation());
        user.setOrganization(request.getOrganization());
        userRepository.save(user);
        return new DTOClass("Profile updated successfully", "SUCCESS", user);
    }
    @Override
    @Transactional
    public DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event not found with ID: " + eventId));
        if (!event.getHost().getEmail().equals(hostEmail)) {
           throw new RuntimeException("Wrong email");
        }
        boolean isAuthorized = event.getHost().getEmail().equals(hostEmail) ||
                event.getAdministrators().stream().anyMatch(admin -> admin.getEmail().equals(hostEmail));

        if (!isAuthorized) {
            throw new RuntimeException("Unauthorized access is not allowed");
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
           throw new RuntimeException("Registration already started");
        }

        eventRepository.save(event);
        return new DTOClass("Event Updated Successfully", "SUCCESS", null);
    }

    private void sendEventUpdateNotifications(Event event) {
        List<User> registeredUsers = registrationRepository.findByEventId(event.getId());
        List<User> favoriteUsers = favouriteRepository.findByEventId(event.getId());

        for (User user : registeredUsers) {
            createNotification(user, event, "The event "+ event.getTitle() +" that you registered for has been updated.");
        }

        for (User user : favoriteUsers) {
            createNotification(user, event, "The event "+ event.getTitle() +" you marked favourite has been updated.");
        }
    }

    @Scheduled(fixedRate = 60000)
    public void notifyEventStart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime notificationWindow = now.plusMinutes(30);

        List<Event> upcomingEvents = eventRepository.findByStartTimeBetweenAndStartNotificationSentFalse(now, notificationWindow);

        for (Event event : upcomingEvents) {
            List<User> registeredUsers = registrationRepository.findByEventId(event.getId());
            List<User> favoriteUsers = favouriteRepository.findByEventId(event.getId());

            String message = "The event " + event.getTitle() + " is starting in 30 minutes!";

            for (User user : registeredUsers) {
                createNotification(user, event, message);
            }

            for (User user : favoriteUsers) {
                createNotification(user, event, message);
            }

            event.setStartNotificationSent(true);
            eventRepository.save(event);
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

    @Transactional
    @Override
    public DTOClass deleteEvent(Long eventId, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Event not found"));
        if (!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Unauthorized to delete this event", "FAILURE", null);
        }
        event.getNotifications().size();
        event.getTimelineEntries().size();

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
            eventResponse.setCategory(event.getCategory());
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

            return new DTOClass("Event Retrieved Successfully", "SUCCESS", eventResponse);
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
        List<TimelineEntryDto> timelineDtos = event.getTimelineEntries().stream()
                .map(entry -> new TimelineEntryDto(entry.getDay(), entry.getDescription()))
                .toList();
        response.setTimelineEntries(timelineDtos);
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
    public DTOClass renameRoom(Long eventId, Long roomId, String newName) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Room room = roomRepository.findByIdAndEventId(roomId, eventId)
                .orElseThrow(() -> new RuntimeException("Room not found for this event"));

        room.setName(newName);
        room.setLastUpdated(LocalDateTime.now());
        roomRepository.save(room);

        return new DTOClass("Room name updated successfully", "SUCCESS", null);
    }

    public DTOClass deleteRoom(Long eventId, Long roomId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Room room = roomRepository.findByIdAndEventId(roomId, eventId)
                .orElseThrow(() -> new RuntimeException("Room not found for this event"));

        roomRepository.delete(room);

        return new DTOClass("Room deleted successfully", "SUCCESS", null);
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

    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    public void sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running sendEventReminders task at {}", now);

        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(now);

        for (Event event : upcomingEvents) {
            try {
                LocalDateTime startTime = event.getStartTime();
                User host = event.getHost();

                long minutesDifference = ChronoUnit.MINUTES.between(now, startTime);

                if (minutesDifference == 1440) { // 24 hours
                    createNotification(host, event, "Reminder: Your event \"" + event.getTitle() + "\" starts in 24 hours.");
                } else if (minutesDifference == 60) { // 1 hour
                    createNotification(host, event, "Reminder: Your event \"" + event.getTitle() + "\" starts in 1 hour.");
                } else if (minutesDifference == 10) { // 10 minutes
                    createNotification(host, event, "Reminder: Your event \"" + event.getTitle() + "\" starts in 10 minutes.");
                }
            } catch (Exception e) {
                log.error("Error sending reminder for event ID {}: {}", event.getId(), e.getMessage());
            }
        }
    }


    @Override
    public List<NotificationDTO> getHostNotifications(String email, int page, int size) {
        User user = getHostByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByParticipantId(user.getId(), pageable);

        List<NotificationDTO> notifications = notificationPage.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return notifications;
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(String.valueOf(notification.getId()));
        dto.setParticipant_id(notification.getParticipant().getId());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRead(notification.isRead());
        notification.setRead(true);
        notificationRepository.save(notification);
        return dto;
    }
    @Override
    public DTOClass assignAdministrators(Long eventId, Long hostId, String hostEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        if (!event.getHost().getEmail().equals(hostEmail)) {
            throw new RuntimeException("Unauthorized to elect administrators"); }
        User adminUser = userRepository.findById(hostId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + hostId));
        if (event.getAdministrators().contains(adminUser)) {
            throw new RuntimeException("User is already an administrator");
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

    @Override
    @Transactional
    public List<TimeLineEntry> addTimelineEntries(Long eventId, BulkTimelineEntryRequest bulkTimelineEntryRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        List<TimeLineEntry> timelineEntries = bulkTimelineEntryRequest.getEntries()
                .stream()
                .map(dto -> TimeLineEntry.builder()
                        .day((long) dto.getDay())
                        .description(dto.getDescription())
                        .event(event)
                        .build())
                .toList();
        return timelineEntryRepository.saveAll(timelineEntries);
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
    public DTOClass getUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        HostDTO hostDTO = new HostDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImageUrl(),
                user.getMobile(),
                user.getRole().name(),
                user.isVerified(),
                user.isEnabled(),
                user.getDesignation(),
                user.getOrganization()
        );
        return new DTOClass("User Fetched Successfully","SUCCESS", hostDTO);
    }
}
