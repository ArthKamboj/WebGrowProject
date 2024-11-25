package com.example.webgrow.Service;

import com.example.webgrow.models.Room;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.request.EventRequest;

import java.util.List;

public interface EventService {
    DTOClass createEvent(EventRequest eventRequest, String email);
    DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail);
    DTOClass deleteEvent(Long eventId,String hostEmail);
    DTOClass getEventList(String hostEmail);
    DTOClass getEventDetails(Long eventId);
    List<Room> getRoomsForEvent(Long eventId);
    DTOClass updateRoomStatus(Long roomId, boolean isVacant);
    DTOClass createRooms(Long eventId, int roomCount, List<String> roomNames);
}
