package com.example.candidatepark.dtos.response;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponse {
    private String token;
    private String message;
    private User user;
    private VerificationStatus emailVerificationStatus;
}
