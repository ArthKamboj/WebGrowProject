package com.example.webgrow.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private LocalDateTime startTime;
    private LocalDateTime endTime;

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

    private boolean isActive;
}
