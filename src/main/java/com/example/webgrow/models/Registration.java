package com.example.webgrow.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "registration",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id","event_id"})
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registration_id;
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private LocalDateTime registrationDate = LocalDateTime.now();

    public Registration(User participant, Event event) {
        this.participant = participant;
        this.event = event;
    }
}