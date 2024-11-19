package com.example.webgrow.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;
    private String title;
    private String mode;
    private String description;
    private String participationType;
    private Long duration;
    private Long participantNumber;
    private String location;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "host_id",nullable = false)
    private User host;
}
