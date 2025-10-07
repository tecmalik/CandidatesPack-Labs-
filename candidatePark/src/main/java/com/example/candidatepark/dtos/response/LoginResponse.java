package com.example.candidatepark.dtos.response;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private User user;
    private String message;
}
