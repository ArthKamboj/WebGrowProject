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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final UserEventViewRepository userEventViewRepository;
    private final QuizRepository quizRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // 1. Get All Events
    @Override
    public ApiResponse<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventRepository.findEvents()
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

        boolean alreadyRegistered = registrationRepository.existsByParticipantAndEvent(user, event);
        if (alreadyRegistered) {
            throw new RuntimeException("User already registered for this event");
        }

        Registration registration = new Registration(user, event);
        registrationRepository.save(registration);

        List<Quiz> quizzes = quizRepository.findByCategory(event.getCategory());
        for (Quiz quiz : quizzes) {
            if (quiz.getId().equals(event.getId()) && !quiz.getParticipants().contains(user)) {
                quiz.getParticipants().add(user);
                quizRepository.save(quiz);
            }
        }

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

        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Event thisEvent = event.get();
            if (thisEvent.getRegisterEnd().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Event registration period has ended");
            }
        }
        else {
            throw new RuntimeException("Event not found");
        }
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
    public ApiResponse<EventDTO> getEventDetails(Long eventId, String email) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        trackEventView(email, event);

        return new ApiResponse<>(true,"Event details retrieved successfully", convertToNewDTO(event));
    }

    @Override
    public ApiResponse<EventDTO> getEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        return new ApiResponse<>(true,"Event details retrieved successfully", convertToNewDTO(event));
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(String.valueOf(notification.getId()));
        dto.setParticipant_id(notification.getParticipant().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRead(notification.isRead());
        notification.setRead(true);
        notificationRepository.save(notification);
        return dto;
    }

    private EventDTO convertToNewDTO(Event event) {
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
        dto.setTeamCreationAllowed(event.isTeamCreationAllowed());
        if(event.isTeamCreationAllowed())
        {
            dto.setMaxTeamSize(event.getMaxTeamSize());
            dto.setMinTeamSize(event.getMinTeamSize());
        }
        dto.setActive(event.isActive());
        List<TimelineEntryDto> timelineDtos = event.getTimelineEntries().stream()
                .map(entry -> new TimelineEntryDto(entry.getDay(), entry.getDescription()))
                .toList();
        dto.setTimelineEntries(timelineDtos);
        dto.setAdministrators(event.getAdministrators());

        EventDTO.HostDTO host = new EventDTO.HostDTO();
        host.setId(String.valueOf(event.getHost().getId()));
        host.setFirstName(event.getHost().getFirstName());
        host.setLastName(event.getHost().getLastName());
        host.setEmail(event.getHost().getEmail());
        host.setMobile(event.getHost().getMobile());
        host.setDesignation(event.getHost().getDesignation());
        host.setOrganization(event.getHost().getOrganization());
        host.setImageUrl(event.getHost().getImageUrl());
        dto.setHost(host);

        return dto;
    }


    private EventDTO convertToDTO(Event event) {
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
                .map(team -> {
                    String leaderId = String.valueOf(team.getLeader().getId());
                    String leaderName = team.getLeader().getFirstName() + " " + team.getLeader().getLastName();
                    String leaderEmail = team.getLeader().getEmail();
                    return new TeamResponse(
                            String.valueOf(team.getId()),
                            team.getName(),
                            leaderId,
                            leaderName,
                            leaderEmail
                    );
                }).collect(Collectors.toList());

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

        TeamJoinRequest joinRequest = new TeamJoinRequest();
                joinRequest.setParticipant(participant);
                joinRequest.setTeam(team);
                joinRequest.setRequestTime(LocalDateTime.now());
                joinRequest.setStatus("PENDING");

        teamJoinRequestRepository.save(joinRequest);

        Notification notification = new Notification();
        notification.setParticipant(team.getLeader());
        notification.setTitle(team.getName());
        notification.setMessage("A participant has requested to join your team: " + team.getName() + " with request id: " + joinRequest.getId());
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        notification.setEvent(team.getEvent());

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

        Notification notification = new Notification();
                notification.setParticipant(joinRequest.getParticipant());
                notification.setTitle(response.toUpperCase());
                notification.setMessage("Your request to join the team: " + joinRequest.getTeam().getName() + " has been " + response.toLowerCase() + ".");
                notification.setTimestamp(LocalDateTime.now());
                notification.setRead(false);
                notification.setEvent(joinRequest.getTeam().getEvent());

        notificationRepository.save(notification);
        return new ApiResponse<>(true, "Request " + response.toLowerCase(), null);
    }

    @Override
    public ApiResponse<TeamDTO> getTeam(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->new RuntimeException("Team does not exist"));

        return new ApiResponse<>(true, "Team details retrieved successfully", convertToDTO(team));
    }

    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setTeamName(team.getName());
        dto.setEvent(team.getEvent());
        dto.setLeader(team.getLeader());
        dto.setMembers(team.getMembers());
        return dto;
    }

    @Override
    public Page<EventDTO> getRecentlyViewedEvents(String email, int page, int size) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));

        Page<Event> events = userEventViewRepository.findRecentEventViewsByUser(email, oneHourAgo, pageable);
        return events.map(this::convertToDTO);
    }

    private void trackEventView(String email, Event event) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        // Check for existing view
        userEventViewRepository.findByUserAndEventAndViewedAtAfter(user, event, oneHourAgo)
                .ifPresentOrElse(
                        view -> {
                            view.setViewedAt(LocalDateTime.now());
                            userEventViewRepository.save(view);
                        },
                        () -> {
                            UserEventView newView = new UserEventView();
                                    newView.setUser(user);
                                    newView.setEvent(event);
                                    newView.setViewedAt(LocalDateTime.now());
                            userEventViewRepository.save(newView);
                        }
                );
    }

    @Override
    public List<EventDTO> getPastRegisteredEvents(String email) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> pastEvents = registrationRepository.findPastRegisteredEvents(email, currentTime);

        return pastEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}