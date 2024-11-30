package com.example.webgrow.payload.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordOtpRequest {

    private String email;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "^[0-9]+$]", message = "OTP must contain only numbers")
    private String otp;
}
