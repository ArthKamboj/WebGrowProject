package com.example.webgrow.payload.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {

    @Email(message = "Email should be valid")
    private String email;
}