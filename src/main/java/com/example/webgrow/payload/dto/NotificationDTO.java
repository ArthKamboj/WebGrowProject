package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private Integer participant_id;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
}
