package com.example.webgrow.Service;

import com.example.webgrow.request.AuthenticateRequest;
import com.example.webgrow.request.RegisterRequest;
import com.example.webgrow.request.ValidatePasswordRequest;
import com.example.webgrow.response.AuthenticateResponse;
import com.example.webgrow.user.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public DTOClass register(RegisterRequest request) throws MessagingException {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        String otp = generateOtp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(), otp);

        return new DTOClass("OTP sent to " + user.getEmail());
    }

    private String generateOtp() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Verification mail";
        String body = "Your verification code is " + otp;
        emailService.sendEmail(email, subject, body);
    }

    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticateResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticateResponse validate(OtpValidate request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp())) {
            var jwtToken = jwtService.generateToken(user);
            return AuthenticateResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new RuntimeException("Invalid OTP provided.");
        }
    }

    public DTOClass forgotPassword(String email) throws MessagingException {
        var user = repository.findByEmail(email).orElseThrow();
        String otp = generateOtp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(), otp);
        return new DTOClass("OTP sent to " + user.getEmail());
    }

    public DTOClass verifyForgotPassword(ValidatePasswordRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return new DTOClass("Password changed successfully");
        } else {
            throw new RuntimeException("Invalid OTP or password mismatch.");
        }
    }
}
