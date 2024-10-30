package com.example.webgrow.controller;


import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.request.AuthenticateRequest;
import com.example.webgrow.request.RegisterRequest;
import com.example.webgrow.response.AuthenticateResponse;
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
    public ResponseEntity<AuthenticateResponse> register (
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate (
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
