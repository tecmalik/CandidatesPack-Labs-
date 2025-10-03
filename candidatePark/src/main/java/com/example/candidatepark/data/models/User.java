package com.example.candidatepark.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name ="USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @NotBlank
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "email_verification_status")
    private EmailVerificationStatus verificationStatus = EmailVerificationStatus.PENDING;

}
