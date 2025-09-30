package com.example.candidatepark.data.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
public class User {
    @Id
    private String id;
    @Email
    private String email;
    @NotBlank
    private String password;

}
