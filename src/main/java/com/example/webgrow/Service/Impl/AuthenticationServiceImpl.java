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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
            if (user.isVerified()) { throw new RuntimeException("User already exists"); }
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
            user.setGeneratedAt(LocalDateTime.now());
            repository.save(user);
            try {
                sendVerificationEmail(user.getEmail(), otp);
            }
            catch (MessagingException e) {
                throw new RuntimeException("Failed to send OTP to " + user.getEmail());
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
            user.setDesignation(request.getDesignation());
            user.setOrganization(request.getOrganization());

            String otp = generateOtp();
            user.setOtp(otp);
            user.setGeneratedAt(LocalDateTime.now());
            repository.save(user);
            try {
                sendVerificationEmail(user.getEmail(), otp);
            }
            catch (MessagingException e) {
                throw new RuntimeException("Failed to send OTP to " + user.getEmail());
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
        String subject = "Verifying your Email address";
        String htmlTemplate = """
    <!DOCTYPE html>
                        <html>
                        <head>
                            <style>
                                body {
                                    font-family: 'Arial', sans-serif;
                                    margin: 0;
                                    padding: 0;
                                    background-color: #f7f9fc;
                                    color: #333333;
                                }
                                .email-container {
                                    max-width: 660px;
                                    margin: 20px auto;
                                    background-color: #ffffff;
                                    border: 1px solid #e0e0e0;
                                    border-radius: 8px;
                                    overflow: hidden;
                                    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
                                }
                                .header {
                                    background-color: #303f9f;
                                    color: #ffffff;
                                    text-align: center;
                                    padding: 25px 0;
                                    font-size: 28px;
                                    font-weight: bold;
                                    letter-spacing: 1px;
                                }
                                .content {
                                    padding: 40px 30px;
                                    text-align: center;
                                    background-color: #f9f9f9;
                                }
                                .content h1 {
                                    font-size: 26px;
                                    font-weight: bold;
                                    color: #303f9f;
                                    margin-bottom: 15px;
                                }
                                .content .otp {
                                    font-size: 40px;
                                    font-weight: bold;
                                    color: #303f9f;
                                    margin: 0 0 20px;
                                    background-color: #e6f0ff;
                                    padding: 15px 30px;
                                    border-radius: 8px;
                                    display: inline-block;
                                    letter-spacing: 2px;
                                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
                                }
                                .content p {
                                    font-size: 14px;
                                    color: #666666;
                                    margin-top: 15px;
                                }
                                .footer {
                                    background-color: #f9f9f9;
                                    padding: 20px;
                                    text-align: center;
                                    font-size: 14px;
                                    color: #999999;
                                    line-height: 1.6;
                                    border-top: 1px solid #e0e0e0;
                                }
                                .footer a {
                                    color: #303f9f;
                                    text-decoration: none;
                                }
                                .footer a:hover {
                                    text-decoration: underline;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="email-container">
                                <div class="header">
                                    WebGrow
                                </div>
                                <div class="content">
                                    <h1>Verification Code</h1>
                                    <div class="otp">%s</div>
                                    <p>(This code will expire 10 minutes after it was sent.)</p>
                                </div>
                                <div class="footer">
                                    Welcome to WebGrow, your trusted platform for managing and discovering events. We’re excited to have you on board!\s
                                    If you have any questions or need assistance, feel free to reach out to our support team.\s
                                    Let’s make your event experience truly exceptional. Thank you for choosing WebGrow – where your events come to life!
                                </div>
                            </div>
                        </body>
                        </html>
""";


        String body = String.format(htmlTemplate, otp);

        emailService.sendEmail(email, subject, body, true);
    }

    public DTOClass authenticate(AuthenticateRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isVerified()) {
            throw new RuntimeException("Failed to send OTP to " + user.getEmail());
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

    private boolean isOtpValid(OtpValidate request ,String inputOtp) {
        User user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.getOtp().equals(request.getOtp())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = Duration.between(user.getGeneratedAt(), now).toMinutes();

        return minutesElapsed <= 10;
    }
    public DTOClass validate(OtpValidate request) {
        User user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (isOtpValid(request,user.getOtp())) {
            var jwtToken = jwtService.generateToken(user);
            user.setVerified(true);
            repository.save(user);
            return new DTOClass("OTP validated successfully", "SUCCESS", new AuthenticateResponse(jwtToken));
        } else {
            throw new RuntimeException("Invalid OTP provided");
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
            throw new RuntimeException("Invalid email provided");
        }
    }

    public DTOClass verifyForgotPasswordOtp(ForgotPasswordOtpRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getOtp().equals(request.getOtp())) {
            user.setEnabled(true);
            repository.save(user);
            return new DTOClass("OTP validated successfully", "SUCCESS", null);
        } else {
            throw new RuntimeException("Invalid OTP provided");
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
                throw new RuntimeException("email not allowed to change password");
            }
        }
        else {
            throw new RuntimeException("Invalid email provided");
        }
    }

}