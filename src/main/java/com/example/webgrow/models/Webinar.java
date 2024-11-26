package com.example.webgrow.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "Webinars")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Webinar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String category;
    private Long capacityMin;
    private Long capacityMax;
    private String eventType;
    private LocalDateTime registerStart;
    private LocalDateTime registerEnd;
    private String festival;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdate;

    private String mode;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToMany
    @JoinTable(
            name = "quiz_participants",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<User> participants;

    @Column(nullable = false)
    private Boolean isActive;
    private String googleMeetLink;
}
