package com.example.webgrow.payload.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String designation;
    private String organization;
}
