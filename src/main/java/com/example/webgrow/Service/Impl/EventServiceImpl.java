package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.Notification;
import com.example.webgrow.models.Quiz;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.response.EventResponse;
import com.example.webgrow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Override
    public DTOClass  createEvent(EventRequest eventRequest, String email) {
        User host = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Host with email " + email + " not found"));
        Event event = new Event();
        event.setTeamCreationAllowed(eventRequest.isTeamCreationAllowed());
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartTime(eventRequest.getStartTime());
        event.setCategory(eventRequest.getCategory());
        event.setEndTime(eventRequest.getEndTime());
        event.setHost(host);
        event.setEventType(eventRequest.getEventType());
        event.setImageUrl(eventRequest.getImageUrl());
        event.setMode(eventRequest.getMode());
        event.setRegisterStart(eventRequest.getRegisterStart());
        event.setRegisterEnd(eventRequest.getRegisterEnd());
        event.setFestival(eventRequest.getFestival());
        event.setCapacityMin(eventRequest.getCapacityMin());
        event.setCapacityMax(eventRequest.getCapacityMax());
        event.setLastUpdate(LocalDateTime.now());
        eventRepository.save(event);

        if (eventRequest.getCategory().toLowerCase().contains("quiz"))
        {
            Quiz quiz = new Quiz();
            quiz.setTitle(eventRequest.getTitle());
            quiz.setDescription(eventRequest.getDescription());
            quiz.setHost(host);
            quiz.setStartTime(eventRequest.getStartTime());
            quiz.setEndTime(eventRequest.getEndTime());
            quiz.setEndTime(eventRequest.getEndTime());
            quiz.setEventType(eventRequest.getEventType());
            quiz.setCategory(eventRequest.getCategory());
            quiz.setRegisterStart(eventRequest.getRegisterStart());
            quiz.setRegisterEnd(eventRequest.getRegisterEnd());
            quiz.setFestival(eventRequest.getFestival());
            quiz.setCapacityMin(eventRequest.getCapacityMin());
            quiz.setCapacityMax(eventRequest.getCapacityMax());
            quiz.setParticipants(event.getParticipants());
            quiz.setIsActive(true);

            quizRepository.save(quiz);
        }
        return new DTOClass("Event Created Successfully","SUCCESS",null);

    }

    @Override
    @Transactional
    public DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if(!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Host Email Not Matched","FAILURE",null);
        }
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setFestival(eventRequest.getFestival());
        event.setCapacityMin(eventRequest.getCapacityMin());
        event.setCapacityMax(eventRequest.getCapacityMax());
        event.setRegisterEnd(eventRequest.getRegisterEnd());
        event.setLastUpdate(LocalDateTime.now());

        sendEventUpdateNotifications(event);
        if(eventRequest.getRegisterStart() != null && !eventRequest.getRegisterStart().isBefore(LocalDateTime.now()))
        {
            event.setRegisterStart(eventRequest.getRegisterStart());
        }
        else{
            return new DTOClass("Registration already started", "ERROR", null);
        }
        eventRepository.save(event);
        return new DTOClass("Event Updated Successfully","SUCCESS",null);
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
        if(!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Unauthorized to delete this event","FAILURE",null);
        }
        eventRepository.delete(event);
        return new DTOClass("Event Deleted Successfully","SUCCESS",null);
    }

    @Override
    public DTOClass getEventList(String hostEmail) {
        User host = userRepository.findByEmail(hostEmail).orElseThrow();
        List<EventResponse> events = eventRepository.findByHostId(host.getId()).stream()
                .map(event -> {
                    EventResponse eventResponse = new EventResponse();
                    eventResponse.setId(event.getId());
                    eventResponse.setTitle(event.getTitle());
                    eventResponse.setDescription(event.getDescription());
                    eventResponse.setLocation(event.getLocation());
                    eventResponse.setMode(event.getMode());
                    eventResponse.setFestival(event.getFestival());
                    eventResponse.setEventType(event.getEventType());
                    eventResponse.setCapacityMin(event.getCapacityMin());
                    eventResponse.setCapacityMax(event.getCapacityMax());
                    eventResponse.setStartTime(event.getStartTime());
                    eventResponse.setEndTime(event.getEndTime());
                    eventResponse.setRegisterStart(event.getRegisterStart());
                    eventResponse.setRegisterEnd(event.getRegisterEnd());
                    eventResponse.setImageUrl(event.getImageUrl());
                    eventResponse.setHostEmail(event.getHost().getEmail());
                    return eventResponse;
                        }

                ).collect(Collectors.toList());
        return new DTOClass("Events Retrieved Successfully","SUCCESS",events);
    }

    @Override
    public DTOClass getEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setLocation(event.getLocation());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
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

    @Override
    public DTOClass getParticipants(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new RuntimeException("Event not found"+eventId));
        List<User> participants = registrationRepository.findByEventId(eventId);
        if(participants.isEmpty()) {
            return new DTOClass("Participant not found","FAILURE",null);
        }
        List <String> participantsDetails = participants.stream()
                .map(user->"Name: "+user.getFirstName()+",Email: "+user.getEmail())
                .collect(Collectors.toList());
        return new DTOClass("Participants retrieved successfully", "SUCCESS", participantsDetails);
    }

}
