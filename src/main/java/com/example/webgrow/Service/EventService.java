package com.example.webgrow.Service;

import com.example.webgrow.models.Notification;
import com.example.webgrow.models.Room;
import com.example.webgrow.models.TimeLineEntry;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.*;
import com.example.webgrow.payload.request.BulkTimelineEntryRequest;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.request.UpdateProfileRequest;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    DTOClass createEvent(EventRequest eventRequest, String email);
    DTOClass updateEvent(Long eventId, EventRequest eventRequest, String hostEmail);
    DTOClass deleteEvent(Long eventId,String hostEmail);
    Page<DTOClass> getEventList(String hostEmail, Pageable pageable);
    DTOClass getEventDetails(Long eventId);
    List<Room> getRoomsForEvent(Long eventId);
    DTOClass updateRoomStatus(Long roomId, boolean isVacant);
    DTOClass createRooms(Long eventId, int roomCount, List<String> roomNames);
    DTOClass getParticipants(Long eventId);
    DTOClass assignAdministrators(Long eventId, Long adminId, String hostEmail);
    List<User> getAdministrators(Long eventId);
    Page<EventDTO> getUnloggedEvents(int page, int size);
    List<TimeLineEntry> addTimelineEntries(Long eventId, BulkTimelineEntryRequest bulkTimelineEntryRequest);
    DTOClass updateUserDetails(UpdateProfileRequest request) throws MessagingException;
    List<Notification> getHostNotifications(String email);
    DTOClass getUserByEmail(String email) throws MessagingException;
}
