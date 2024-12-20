package com.example.webgrow.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String role;
    private String imageUrl;
    private boolean verified;
    private boolean enabled;
    private String organization;
    private String designation;
}
