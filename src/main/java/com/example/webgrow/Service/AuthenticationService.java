package com.example.webgrow.Service;

import com.example.webgrow.models.User;
import com.example.webgrow.payload.request.AuthenticateRequest;
import com.example.webgrow.payload.request.ForgotPasswordOtpRequest;
import com.example.webgrow.payload.request.RegisterRequest;
import com.example.webgrow.payload.request.ValidatePasswordRequest;
import com.example.webgrow.models.DTOClass;
import com.example.webgrow.payload.request.OtpValidate;
import jakarta.mail.MessagingException;

public interface AuthenticationService {
    DTOClass register(RegisterRequest request);
    DTOClass authenticate(AuthenticateRequest request);
    DTOClass validate(OtpValidate request);
    DTOClass forgotPassword(String email) throws MessagingException;
    DTOClass verifyForgotPasswordOtp(ForgotPasswordOtpRequest request) throws MessagingException;
    DTOClass verifyForgotPassword(ValidatePasswordRequest request);
    DTOClass getUserByEmail(String email) throws MessagingException;
}
