package com.example.candidatepark.services;


import com.example.candidatepark.dtos.SignUpResponse;
import com.example.candidatepark.dtos.UserDTO;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface UserServices {
    SignUpResponse signUp(UserDTO testUser);
}
