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

    public String register(RegisterRequest request) throws MessagingException {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();


        String otp= generateotp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(),otp);

        return ("OTP sent to "+user.getEmail());
    }

    public String hostRegister(HostRegisterRequest request) throws MessagingException {
        var host = Host.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .organization(request.getOrganization())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.HOST)
                .build();

        String otp= generateotp();
        host.setOtp(otp);
        hostRepository.save(host);
        sendVerificationEmail(host.getEmail(),otp);

        return ("OTP sent to "+host.getEmail());
    }

    private String generateotp(){
        Random random=new Random();
        int otpvalue= 1000+random.nextInt(9000);
        return String.valueOf(otpvalue);
    }
    private void sendVerificationEmail(String Email,String otp) throws MessagingException {
        String subject="Verification mail";
        String body="Your verification code is "+otp;
        emailService.sendEmail(Email,subject,body);
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
        var user=repository.findByEmail(request.getEmail()).orElseThrow();
        if(user.getOtp().equals(request.getOtp())){
            var jwtToken = jwtService.generateToken(user);
            return AuthenticateResponse.builder()
                    .token(jwtToken)
                    .build();
        }else {
            throw new RuntimeException("Invalid OTP provided.");
        }

    }

    public String forgotPassword(String email) throws MessagingException {
        var user = repository.findByEmail(email).orElseThrow();
        String otp= generateotp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(),otp);
        return ("OTP sent to "+user.getEmail());
    }

    public String verifyForgotPassword(ValidatePasswordRequest request) {
        var user=repository.findByEmail(request.getEmail()).orElseThrow();
        if(user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword()) ){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return ("Password changed successfully");
        }
        else {
            throw new RuntimeException("Invalid OTP provided.");
        }
    }
}
