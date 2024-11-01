package com.example.webgrow.Service;

import com.example.webgrow.request.AuthenticateRequest;
import com.example.webgrow.request.HostRegisterRequest;
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
    private final HostRepository hostRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public DTOClass register(RegisterRequest request) throws MessagingException {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        String otp = generateOtp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(), otp);

        return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
    }

    private String generateOtp() {
        Random random = new Random();
        int otpValue = 1000 + random.nextInt(9000);
        return String.valueOf(otpValue);
    }

    public DTOClass hostRegister(HostRegisterRequest request) throws MessagingException {
        var host = Host.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .organization(request.getOrganization())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.HOST)
                .build();

        String otp = generateOtp();
        host.setOtp(otp);
        hostRepository.save(host);
        sendVerificationEmail(host.getEmail(), otp);

        return new DTOClass("OTP sent to " + host.getEmail(), "SUCCESS", null);
    }

    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Verification mail";
        String body = "Your verification code is " + otp;
        emailService.sendEmail(email, subject, body);
    }

    public DTOClass authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new DTOClass("Authentication successful", "SUCCESS", new AuthenticateResponse(jwtToken));
    }

    public DTOClass validate(OtpValidate request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp())) {
            var jwtToken = jwtService.generateToken(user);
            return new DTOClass("OTP validated successfully", "SUCCESS", new AuthenticateResponse(jwtToken));
        } else {
            return new DTOClass("Invalid OTP provided", "FAILURE", null);
        }
    }

    public DTOClass forgotPassword(String email) throws MessagingException {
        var user = repository.findByEmail(email).orElseThrow();
        String otp = generateOtp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(), otp);
        return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
    }

    public DTOClass verifyForgotPassword(ValidatePasswordRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return new DTOClass("Password changed successfully", "SUCCESS", null);
        } else {
            return new DTOClass("Invalid OTP or password mismatch", "FAILURE", null);
        }
    }
}
