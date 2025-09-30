package com.example.candidatepark.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {
    @Id
    private String id;
    private String email;
    private String password;

}
