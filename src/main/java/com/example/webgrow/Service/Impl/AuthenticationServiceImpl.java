package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.AuthenticationService;
import com.example.webgrow.Service.EmailService;
import com.example.webgrow.Service.JwtService;
import com.example.webgrow.models.*;
import com.example.webgrow.payload.dto.DTOClass;
import com.example.webgrow.payload.request.*;
import com.example.webgrow.payload.response.AuthenticateResponse;
import com.example.webgrow.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public DTOClass register(RegisterRequest request) {

        String contact = request.getEmail();
        User user = new User();
        if (repository.existsByEmail(contact)) {
            String password = request.getPassword();
            user = repository.findByEmail(contact).orElseThrow();
            if (user.isVerified()) { return new DTOClass("User Already Exists", "ERROR", null); }
            user.setFirstName(request.getFirstname());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setVerified(false);
            user.setEnabled(false);
            user.setPassword(passwordEncoder.encode(password));
            String otp = generateOtp();
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            user.setDesignation(request.getDesignation());
            user.setOrganization(request.getOrganization());
            user.setOtp(otp);
            repository.save(user);
            try {
                sendVerificationEmail(user.getEmail(), otp);
            }
            catch (MessagingException e) {
                return new DTOClass("Failed to send otp", "ERROR", null);
            }
            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        } else {
            user.setFirstName(request.getFirstname());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setVerified(false);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));

            String otp = generateOtp();
            user.setOtp(otp);
            repository.save(user);
            try {
                sendVerificationEmail(user.getEmail(), otp);
            }
            catch (MessagingException e) {
                return new DTOClass("Failed to send otp", "ERROR", null);
            }

            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otpValue = 1000 + random.nextInt(9000);
        return String.valueOf(otpValue);
    }



    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Verification mail";
        String body = "Your verification code is " + otp;
        emailService.sendEmail(email, subject, body);
    }

    public DTOClass authenticate(AuthenticateRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isVerified()) {
            return new DTOClass("Invalid email", "ERROR", null);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        return new DTOClass("Authentication successful. Role: "+ user.getRole(), "SUCCESS", new AuthenticateResponse(jwtToken));
    }

    public DTOClass validate(OtpValidate request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp())) {
            var jwtToken = jwtService.generateToken(user);
            user.setVerified(true);
            repository.save(user);
            return new DTOClass("OTP validated successfully", "SUCCESS", new AuthenticateResponse(jwtToken));
        } else {
            return new DTOClass("Invalid OTP provided", "FAILURE", null);
        }
    }

    public DTOClass forgotPassword(String email) throws MessagingException {
        if(repository.existsByEmail(email)) {
            var user = repository.findByEmail(email).orElseThrow();
            String otp = generateOtp();
            user.setOtp(otp);
            repository.save(user);
            sendVerificationEmail(email, otp);
            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        }
        else {
//            var user = hostRepository.findByEmail(email).orElseThrow();
//            String otp = generateOtp();
//            user.setOtp(otp);
//            hostRepository.save(user);
//            sendVerificationEmail(email, otp);
            return new DTOClass("NO USER WITH GIVEN EMAIL FOUND " , "FAILURE", null);
        }
    }

    public DTOClass verifyForgotPasswordOtp(ForgotPasswordOtpRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp())) {
            user.setEnabled(true);
            repository.save(user);
            return new DTOClass("OTP validated successfully", "SUCCESS", null);
        } else {
            return new DTOClass("Invalid OTP provided", "FAILURE", null);
        }
    }

    public DTOClass verifyForgotPassword(ValidatePasswordRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            var user = repository.findByEmail(request.getEmail()).orElseThrow();
            if(user.isEnabled()) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setEnabled(false);
                repository.save(user);
                return new DTOClass("Password changed successfully", "SUCCESS", null);
            }
            else {
                return new DTOClass("Email not verified through otp", "FAILURE", null);
            }
        }
        else {
//            var user = hostRepository.findByEmail(request.getEmail()).orElseThrow();
//            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//            hostRepository.save(user);
            return new DTOClass("NO USER EXIST WITH THE GIVEN EMAIL", "FAILURE", null);
        }
    }
    public DTOClass getUserByEmail(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        HostDTO hostDTO = new HostDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                user.getRole().name(),
                user.isVerified(),
                user.isEnabled()
        );
        return new DTOClass("User Fetched Successfully","SUCCESS", hostDTO);
    }



}
