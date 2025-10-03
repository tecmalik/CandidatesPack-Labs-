package com.example.candidatepark.services;


import com.example.candidatepark.dtos.request.LoginResponse;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.request.UserDTO;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface UserServices {
    SignUpResponse signUp(UserDTO testUser);
    LoginResponse login(UserDTO testUser);

}
