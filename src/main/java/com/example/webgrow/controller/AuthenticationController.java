package com.example.webgrow.controller;

import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.models.User;
import com.example.webgrow.payload.request.*;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.request.OtpValidate;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<DTOClass> register(
            @Valid @RequestBody RegisterRequest request
    ) {
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

    @GetMapping("/user/profile")
    public ResponseEntity<DTOClass> getProfile() throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        DTOClass user=service.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/updateprofile")
    public ResponseEntity<DTOClass> updateUserDetails(@RequestBody UpdateProfileRequest request) throws MessagingException {
        DTOClass user=service.updateUserDetails(request);
        return ResponseEntity.ok(user);
    }
}