package com.example.webgrow.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue
    private Integer id;
    private long participantId;
    private long eventId;

    public Favourite(Integer id, Long eventId) {

        this.id = id;
        this.eventId = eventId;
    }
}
