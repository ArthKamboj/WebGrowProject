package com.example.webgrow.controller;

import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.request.*;
import com.example.webgrow.response.AuthenticateResponse;
import com.example.webgrow.user.DTOClass;
import com.example.webgrow.user.OtpValidate;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<DTOClass> register(
            @RequestBody RegisterRequest request
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

    @PutMapping("/verify-otp")
    public ResponseEntity<DTOClass> verify(
            @RequestBody ForgotPswrdOtpRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.verifyForgotPswrdOtp(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<DTOClass> changePassword(
            @RequestBody ValidatePasswordRequest request
    ) {
        return ResponseEntity.ok(service.verifyForgotPassword(request));
    }
}
