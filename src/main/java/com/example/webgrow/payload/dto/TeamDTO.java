package com.example.webgrow.payload.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private String teamName;
    private EventDTO event;
    private UserDTO leader;
    private List<UserDTO> members;
}
