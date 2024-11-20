package com.example.webgrow.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "favourite")
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favId;
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
