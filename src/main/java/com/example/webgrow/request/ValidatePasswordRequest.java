package com.example.webgrow.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidatePasswordRequest {

    private String email;
    private String otp;
    private String newPassword;
}
