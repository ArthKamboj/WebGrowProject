package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.models.DTOClass;
import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.response.EventResponse;
import com.example.webgrow.repository.EventRepository;
import com.example.webgrow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public DTOClass createEvent(EventRequest eventRequest, String hostEmail) {
        User host = userRepository.findByEmail(hostEmail).orElseThrow();
        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setDate(eventRequest.getDate());
        event.setTime(eventRequest.getTime());
        event.setHost(host);
        eventRepository.save(event);
        return new DTOClass("Event Created Successfully","SUCCESS",null);
    }

    @Override
    public DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        if(!event.getHost().getEmail().equals(hostEmail)) {
            return new DTOClass("Host Email Not Matched","FAILURE",null);
        }
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setDate(eventRequest.getDate());
        event.setTime(eventRequest.getTime());
        eventRepository.save(event);
        return new DTOClass("Event Updated Successfully","SUCCESS",null);
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
                    eventResponse.setDate(event.getDate());
                    eventResponse.setTime(event.getTime());
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
        response.setDate(event.getDate());
        response.setTime(event.getTime());
        response.setHostEmail(event.getHost().getEmail());
        return new DTOClass("Event details retrieved successfully", "SUCCESS", response);
    }
}
