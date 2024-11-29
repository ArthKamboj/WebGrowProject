package com.example.webgrow.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String role;
    private String imageUrl;
    private boolean verified;
    private boolean enabled;
}
