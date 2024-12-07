package com.example.webgrow.controller;

import com.example.webgrow.Service.EventService;
import com.example.webgrow.models.Role;
import com.example.webgrow.models.Room;
import com.example.webgrow.models.TimeLineEntry;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.dto.NotificationDTO;
import com.example.webgrow.payload.dto.RoomRenameDTO;
import com.example.webgrow.payload.dto.UserDTO;
import com.example.webgrow.payload.request.BulkTimelineEntryRequest;
import com.example.webgrow.payload.request.EventRequest;
import com.example.webgrow.payload.request.UpdateProfileRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<DTOClass> createEvent(@RequestBody EventRequest eventRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DTOClass response = eventService.createEvent(eventRequest, email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DTOClass> updateEvent(@RequestBody EventRequest eventRequest, @PathVariable("id") long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DTOClass response = eventService.updateEvent(id, eventRequest, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DTOClass> deleteEvent(@PathVariable("id") long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DTOClass response = eventService.deleteEvent(id, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<DTOClass>> getAllEvents( Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Page<DTOClass> response = eventService.getEventList(email, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOClass> getEvent(@PathVariable("id") long id) {
        DTOClass response = eventService.getEventDetails(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/events/{eventId}/rooms/create")
    public ResponseEntity<DTOClass> createRooms(
            @PathVariable Long eventId,
            @RequestParam int roomCount,
            @RequestBody(required = false) List<String> roomNames) {
        DTOClass response = eventService.createRooms(eventId, roomCount, roomNames);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/rooms/{roomId}/status")
    public ResponseEntity<DTOClass> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam boolean isVacant) {
        DTOClass response = eventService.updateRoomStatus(roomId, isVacant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{eventId}/rooms")
    public ResponseEntity<List<Room>> getRoomsForEvent(@PathVariable Long eventId) {
        List<Room> rooms = eventService.getRoomsForEvent(eventId);
        return ResponseEntity.ok(rooms);
    }

    @PatchMapping("/events/{eventId}/rooms/{roomId}")
    public ResponseEntity<DTOClass> renameRoom(
            @PathVariable Long eventId,
            @PathVariable Long roomId,
            @RequestBody RoomRenameDTO roomRenameDTO) {
        if (roomRenameDTO.getName() == null || roomRenameDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Room name cannot be null or blank");
        }

        DTOClass response = eventService.renameRoom(eventId, roomId, roomRenameDTO.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{eventId}/rooms/{roomId}/delete")
    public ResponseEntity<DTOClass> deleteRoom(
            @PathVariable Long eventId,
            @PathVariable Long roomId) {
        DTOClass response = eventService.deleteRoom(eventId, roomId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}/participants")
    public ResponseEntity<DTOClass> getParticipantsForEvent(@PathVariable("id") Long id) {
        DTOClass response = eventService.getParticipants(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("{eventId}/add-admin/{adminId}")
    public ResponseEntity<DTOClass> addAdmin(@PathVariable Long eventId,
                                             @PathVariable Long adminId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DTOClass response =eventService.assignAdministrators(eventId,adminId,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<DTOClass> getProfile() throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        DTOClass user = eventService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/updateprofile")
    public ResponseEntity<DTOClass> updateUserDetails(@RequestBody UpdateProfileRequest request) throws MessagingException {
        DTOClass user = eventService.updateUserDetails(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/notifications/{page}/{size}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Integer page, @PathVariable Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        int defaultPage = (page != null) ? page : 0;
        int defaultSize = (size != null) ? size : 10;
        List<NotificationDTO> notifications = eventService.getHostNotifications(email, defaultPage, defaultSize);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("{eventId}/administrators")
    public ResponseEntity<List<User>> getAdministrators(@PathVariable Long eventId) {
        List<User> administrators = eventService.getAdministrators(eventId);
        return ResponseEntity.ok(administrators);
    }

    @PostMapping("/{eventId}/timeline/bulk")
    public ResponseEntity<List<TimeLineEntry>> addTimelineEntry(
            @PathVariable Long eventId,
            @RequestBody BulkTimelineEntryRequest bulkTimelineEntryRequest
    ) {
        List<TimeLineEntry> newEntries = eventService.addTimelineEntries(eventId, bulkTimelineEntryRequest);
        return ResponseEntity.ok(newEntries);
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        List<UserDTO> users = eventService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

}
