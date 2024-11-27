package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.ParticipantService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.*;
import com.example.webgrow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final TeamRepository teamRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // 1. Get All Events
    @Override
    public ApiResponse<List<EventDTO>> getAllEvents(String search, String category, String location) {
        List<EventDTO> events = eventRepository.findEvents(search, category, location)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "Events retrieved successfully", events);
    }

    // 2. Get Registered Events
    @Override
    public ApiResponse<List<EventDTO>> getRegisteredEvents(String email) {
        User user = getUserByEmail(email);
        List<EventDTO> registeredEvents = registrationRepository.findByParticipantId(user.getId())
                .stream()
                .map(registration -> convertToDTO(registration.getEvent()))
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "Registered events retrieved successfully", registeredEvents);
    }

    // 3. Register for an Event
    @Override
    @Transactional
    public ApiResponse<String> registerForEvent(String email, Long eventId) {
        User user = getUserByEmail(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = new Registration(user, event);
        registrationRepository.save(registration);

        return new ApiResponse<>(true,"Successfully registered for the event", null);
    }

    // 4. Add to Favourites
    @Override
    @Transactional
    public ApiResponse<String> addToFavourites(String email, Long eventId) {
        User user = getUserByEmail(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Favourite favourite = new Favourite(null, user, event);
        favouriteRepository.save(favourite);

        return new ApiResponse<>(true, "Event added to favourites", null);
    }

    // 5. Get Favourite Events
    @Override
    public ApiResponse<List<EventDTO>> getFavouriteEvents(String email) {
        User user = getUserByEmail(email);
        List<EventDTO> favEvents = favouriteRepository.findByParticipantId(user.getId())
                .stream()
                .map(favourite -> convertToDTO(favourite.getEvent()))
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "Favourite events retrieved successfully", favEvents);
    }

    // 6. Unregister from an Event
    @Override
    @Transactional
    public ApiResponse<String> unregisterFromEvent(String email, Long eventId) {
        User user = getUserByEmail(email);
        Registration registration = registrationRepository
                .findByParticipantIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registrationRepository.delete(registration);

        return new ApiResponse<>(true,"Successfully unregistered from the event", null);
    }

    // 7. Unmark an Event as Favourite
    @Override
    @Transactional
    public ApiResponse<String> unmarkAsFavourite(String email, Long eventId) {
        User user = getUserByEmail(email);
        Favourite favourite = favouriteRepository
                .findByParticipantIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new RuntimeException("Favourite not found"));

        favouriteRepository.delete(favourite);

        return new ApiResponse<>(true, "Event successfully removed from favourites", null);
    }

    // 8. Get Participant Profile
    @Override
    public ApiResponse<UserDTO> getParticipantProfile(String email) {
        User user = getUserByEmail(email);
        return new ApiResponse<>(true,"Participant profile retrieved successfully", UserDTO.from(user));
    }

    // 9. Update Participant Profile
    @Override
    @Transactional
    public ApiResponse<String> updateParticipantProfile(String email, User updatedProfile) {
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
        if(updatedProfile.getImageUrl() != null) {
            user.setImageUrl(updatedProfile.getImageUrl());
        }

        userRepository.save(user);
        return new ApiResponse<>(true, "Profile updated successfully", null);
    }

    // 10. Get Notifications
    @Override
    public ApiResponse<List<NotificationDTO>> getNotifications(String email, int page, int size) {
        User user = getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByParticipantId(user.getId(), pageable);

        List<NotificationDTO> notifications = notificationPage.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "Notifications retrieved successfully", notifications);
    }

    @Override
    public ApiResponse<EventDTO> getEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        return new ApiResponse<>(true,"Event details retrieved successfully", convertToDTO(event));
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setParticipant_id(notification.getParticipant().getId());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRead(notification.isRead());
        return dto;
    }


    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
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

    @Transactional
    public ApiResponse<String> createTeam(String email, Long eventId, TeamRequest teamRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.isTeamCreationAllowed()) {
            throw new RuntimeException("Team creation is not allowed for this event.");
        }

        User leader = getUserByEmail(email);

        if (!teamRepository.findByEventIdAndMembers_Id(eventId, leader.getId()).isEmpty()) {
            throw new RuntimeException("User already part of a team.");
        }

        Team team = Team.builder()
                .name(teamRequest.getTeamName())
                .event(event)
                .leader(leader)
                .members(List.of(leader))
                .isPublic(teamRequest.isPublic())
                .build();

        teamRepository.save(team);
        return new ApiResponse<>(true, "Team created successfully", null);
    }

    @Transactional
    public ApiResponse<List<TeamResponse>> searchTeams(Long eventId, String teamName) {
        List<Team> teams = (teamName != null && !teamName.isEmpty())
                ? teamRepository.findByEventIdAndIsPublicTrueAndNameContainingIgnoreCase(eventId, teamName)
                : teamRepository.findByEventIdAndIsPublicTrue(eventId);

        List<TeamResponse> teamResponses = teams.stream()
                .map(team -> new TeamResponse(team.getId(), team.getName(), team.getLeader().getId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "Teams retrieved successfully", teamResponses);
    }


    @Transactional
    public ApiResponse<String> requestToJoinTeam(String email, Long teamId) {
        User participant = getUserByEmail(email);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (team.getMembers().contains(participant)) {
            throw new RuntimeException("User already in the team.");
        }

        TeamJoinRequest joinRequest = TeamJoinRequest.builder()
                .participant(participant)
                .team(team)
                .requestTime(LocalDateTime.now())
                .status("PENDING")
                .build();

        teamJoinRequestRepository.save(joinRequest);

        Notification notification = Notification.builder()
                .participant(team.getEvent().getHost())
                .message("A participant has requested to join the team: " + team.getName())
                .timestamp(LocalDateTime.now())
                .read(false)
                .event(team.getEvent())
                .build();

        notificationRepository.save(notification);
        return new ApiResponse<>(true, "Join request sent to host", null);
    }


    @Transactional
    public ApiResponse<String> respondToJoinRequest(Long requestId, String response) {
        TeamJoinRequest joinRequest = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found"));

        if (!response.equalsIgnoreCase("APPROVED") && !response.equalsIgnoreCase("REJECTED")) {
            throw new RuntimeException("Invalid response. Use APPROVED or REJECTED.");
        }

        joinRequest.setStatus(response.toUpperCase());
        teamJoinRequestRepository.save(joinRequest);

        if (response.equalsIgnoreCase("APPROVED")) {
            Team team = joinRequest.getTeam();
            team.getMembers().add(joinRequest.getParticipant());
            teamRepository.save(team);
        }

        Notification notification = Notification.builder()
                .participant(joinRequest.getParticipant())
                .message("Your request to join the team: " + joinRequest.getTeam().getName() + " has been " + response.toLowerCase() + ".")
                .timestamp(LocalDateTime.now())
                .read(false)
                .event(joinRequest.getTeam().getEvent())
                .build();

        notificationRepository.save(notification);
        return new ApiResponse<>(true, "Request " + response.toLowerCase(), null);
    }


}
