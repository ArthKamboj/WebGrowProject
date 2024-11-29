package com.example.webgrow.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String category;
    private Long capacityMin;
    private Long capacityMax;
    private String eventType;
    private LocalDateTime registerStart;
    private LocalDateTime registerEnd;
    private String festival;
    private boolean teamCreationAllowed;
    private int minTeamSize;
    private int maxTeamSize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdate;
    private String organization;

    private String mode;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<User> participants;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favourite> favourites = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_administrators",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    @JsonManagedReference
    private List<User> administrators = new ArrayList<>();
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEventView> userEventViews = new ArrayList<>();


    private String url;
    private boolean isActive;
}
