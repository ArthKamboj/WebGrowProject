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
@Table(name = "registration")
public class Registration {

    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    @JoinColumn(name = "id")
    private User participant;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime registrationDate = LocalDateTime.now();

    public Registration(User participant, Event event) {
        this.participant = participant;
        this.event = event;
    }

    public Registration(Integer id, Long eventId) {

        this.id = id;
        this.event = Event.builder().id(eventId).build();
    }
}