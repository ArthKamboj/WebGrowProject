package com.example.webgrow.Service;

import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.DTOClass;
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
    DTOClass renameRoom(Long eventId, Long roomId, String newName);
    DTOClass deleteRoom(Long eventId, Long roomId);
    DTOClass createRooms(Long eventId, int roomCount, List<String> roomNames);
    DTOClass getParticipants(Long eventId);
    DTOClass assignAdministrators(Long eventId, Long adminId, String hostEmail);
    List<User> getAdministrators(Long eventId);
    List<EventDTO> getUnloggedEvents();
    List<TimeLineEntry> addTimelineEntries(Long eventId, BulkTimelineEntryRequest bulkTimelineEntryRequest);
    DTOClass updateUserDetails(UpdateProfileRequest request) throws MessagingException;
    List<NotificationDTO> getHostNotifications(String email, int page, int size);
    DTOClass getUserByEmail(String email) throws MessagingException;
    List<UserDTO> getUsersByRole(Role role);
}
