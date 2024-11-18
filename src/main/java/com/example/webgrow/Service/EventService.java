package com.example.webgrow.Service;

import com.example.webgrow.models.DTOClass;
import com.example.webgrow.payload.request.EventRequest;

public interface EventService {
    DTOClass createEvent(EventRequest eventRequest, String email);
    DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail);
    DTOClass deleteEvent(Long eventId,String hostEmail);
    DTOClass getEventList(String hostEmail);
    DTOClass getEventDetails(Long eventId);
}
