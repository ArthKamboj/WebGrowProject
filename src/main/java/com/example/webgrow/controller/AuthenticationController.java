package com.example.webgrow.controller;


import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.request.AuthenticateRequest;
import com.example.webgrow.request.RegisterRequest;
import com.example.webgrow.response.AuthenticateResponse;
import com.example.webgrow.user.OtpValidate;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
