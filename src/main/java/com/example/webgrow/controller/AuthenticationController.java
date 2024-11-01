package com.example.webgrow.controller;


import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.request.*;
import com.example.webgrow.response.AuthenticateResponse;
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
    public ResponseEntity<String> register (
            @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("register-host")
    public ResponseEntity<String> registerHost (
            @RequestBody HostRegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.hostRegister(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate (
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/validate")
    public ResponseEntity<AuthenticateResponse> validate(
            @RequestBody OtpValidate request
    ){
        return ResponseEntity.ok(service.validate(request));
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgetPasswordRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.forgotPassword(request.getEmail()));
    }
    @PutMapping("/verify-otp")
    public ResponseEntity<String> verify(
            @RequestBody ValidatePasswordRequest otp
    ){
        return ResponseEntity.ok(service.verifyForgotPassword(otp));
    }
}
