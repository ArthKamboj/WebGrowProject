package com.example.webgrow.Service;

import com.example.webgrow.request.*;
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

        String contact = request.getEmail();
        if (repository.existsByEmail(contact)) {
            String password = request.getPassword();
            var user = repository.findByEmail(contact).orElseThrow();
            user.setFirstName(request.getFirstname());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setVerified(false);
            user.setPassword(passwordEncoder.encode(password));
            String otp = generateOtp();
            user.setOtp(otp);
            repository.save(user);
            sendVerificationEmail(user.getEmail(), otp);
            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        } else {
            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .mobile(request.getMobile())
                    .verified(false)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();


            String otp = generateOtp();
            user.setOtp(otp);
            repository.save(user);
            sendVerificationEmail(user.getEmail(), otp);

            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otpValue = 1000 + random.nextInt(9000);
        return String.valueOf(otpValue);
    }

    public DTOClass hostRegister(HostRegisterRequest request) throws MessagingException {
        String contact = request.getEmail();
        if (hostRepository.existsByEmail(contact)) {
            String password = request.getPassword();
            var host = hostRepository.findByEmail(contact).orElseThrow();
            host.setFirstName(request.getFirstname());
            host.setLastName(request.getLastname());
            host.setEmail(request.getEmail());
            host.setMobile(request.getMobile());
            host.setVerified(false);
            host.setOrganization(request.getOrganization());
            host.setDesignation(request.getDesignation());
            host.setPassword(passwordEncoder.encode(password));
            String otp = generateOtp();
            host.setOtp(otp);
            hostRepository.save(host);
            sendVerificationEmail(host.getEmail(), otp);
            return new DTOClass("OTP sent to " + host.getEmail(), "SUCCESS", null);
        } else {
            var host = Host.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .mobile(request.getMobile())
                    .organization(request.getOrganization())
                    .designation(request.getDesignation())
                    .verified(false)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.HOST)
                    .build();

            String otp = generateOtp();
            host.setOtp(otp);
            hostRepository.save(host);
            sendVerificationEmail(host.getEmail(), otp);

            return new DTOClass("OTP sent to " + host.getEmail(), "SUCCESS", null);
        }
    }

    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Verification mail";
        String body = "Your verification code is " + otp;
        emailService.sendEmail(email, subject, body);
    }

    public DTOClass authenticate(AuthenticateRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isVerified()) {
            return new DTOClass("Invalid email or password", "ERROR", null);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        return new DTOClass("Authentication successful", "SUCCESS", new AuthenticateResponse(jwtToken));
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
            var user = hostRepository.findByEmail(email).orElseThrow();
            String otp = generateOtp();
            user.setOtp(otp);
            hostRepository.save(user);
            sendVerificationEmail(email, otp);
            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
        }
//            return new DTOClass("Invalid OTP provided", "FAILURE", null);
//            var user = repository.existsByEmail(email)? repository.findByEmail(email).orElseThrow() : hostRepository.findByEmail(email).orElseThrow();
//            String otp = generateOtp();
//            user.setOtp(otp);
//            repository.save(user);
//            sendVerificationEmail(user.getEmail(), otp);
//            return new DTOClass("OTP sent to " + user.getEmail(), "SUCCESS", null);
//        }
    }

    public DTOClass verifyForgotPassword(ValidatePasswordRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            var user = repository.findByEmail(request.getEmail()).orElseThrow();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return new DTOClass("Password changed successfully", "SUCCESS", null);
        }
        else {
            var user = hostRepository.findByEmail(request.getEmail()).orElseThrow();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            hostRepository.save(user);
            return new DTOClass("Password changed successfully", "SUCCESS", null);
        }
    }

    public DTOClass validateHostOtp(ValidateHost request) {
        var host = hostRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Host not found"));

        if (host.getOtp().equals(request.getOtp())) {
            var jwtToken = jwtService.generateToken(host);
            return new DTOClass("Host OTP validated successfully", "SUCCESS", new AuthenticateResponse(jwtToken));
        } else {
            return new DTOClass("Invalid OTP provided", "FAILURE", null);
        }
    }
}
