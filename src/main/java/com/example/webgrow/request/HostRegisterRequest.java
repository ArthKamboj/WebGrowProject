package com.example.webgrow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HostRegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String organization;
    private String designation;
    private String password;
}

