package com.example.webgrow.payload.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpValidate {
    private String email;

    @NotBlank(message = "OTP cannot be empty")
    @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only numbers")
    private String otp;
}
