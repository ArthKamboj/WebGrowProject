package com.example.webgrow.payload.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Invalid mobile number")
    private String mobile;
    private String designation;
    private String organization;
    private String imageUrl;
}
