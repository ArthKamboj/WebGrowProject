package com.example.webgrow.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {

    private String email;
}