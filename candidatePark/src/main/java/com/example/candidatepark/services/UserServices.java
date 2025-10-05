package com.example.candidatepark.services;


import com.example.candidatepark.dtos.response.LoginResponse;
import com.example.candidatepark.dtos.request.TokenDTO;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.VerificationResponseDTO;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface UserServices {
    SignUpResponse signUp(UserDTO testUser);
    LoginResponse login(UserDTO testUser);
    VerificationResponseDTO verifyEmail(TokenDTO tokenDTO);

}
