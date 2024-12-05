package com.example.webgrow.payload.dto;

import com.example.webgrow.models.Event;
import com.example.webgrow.models.User;
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
    private Event event;
    private User leader;
    private List<User> members;
}
