package com.example.webgrow.controller;

import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.payload.request.*;
import com.example.webgrow.models.DTOClass;
import com.example.webgrow.payload.request.OtpValidate;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<DTOClass> register(
            @Valid @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<DTOClass> authenticate(
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<DTOClass> validate(
            @RequestBody OtpValidate request
    ) {
        return ResponseEntity.ok(service.validate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DTOClass> forgotPassword(
            @RequestBody ForgetPasswordRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.forgotPassword(request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<DTOClass> verify(
            @RequestBody ForgotPasswordOtpRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.verifyForgotPasswordOtp(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<DTOClass> changePassword(
            @Valid @RequestBody ValidatePasswordRequest request
    ) {
        return ResponseEntity.ok(service.verifyForgotPassword(request));
    }
}
