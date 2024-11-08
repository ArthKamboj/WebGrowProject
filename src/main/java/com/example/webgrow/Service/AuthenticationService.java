package com.example.webgrow.Service;

import com.example.webgrow.payload.request.AuthenticateRequest;
import com.example.webgrow.payload.request.ForgotPswrdOtpRequest;
import com.example.webgrow.payload.request.RegisterRequest;
import com.example.webgrow.payload.request.ValidatePasswordRequest;
import com.example.webgrow.models.DTOClass;
import com.example.webgrow.models.OtpValidate;
import jakarta.mail.MessagingException;

public interface AuthenticationService {
    DTOClass register(RegisterRequest request);
    DTOClass authenticate(AuthenticateRequest request);
    DTOClass validate(OtpValidate request);
    DTOClass forgotPassword(String email) throws MessagingException;
    DTOClass verifyForgotPswrdOtp(ForgotPswrdOtpRequest request) throws MessagingException;
    DTOClass verifyForgotPassword(ValidatePasswordRequest request);

}
