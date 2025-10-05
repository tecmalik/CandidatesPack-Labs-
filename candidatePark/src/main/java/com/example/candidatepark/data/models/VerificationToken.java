package com.example.candidatepark.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;


    public VerificationToken() {
        this.expiryDate = LocalDateTime.now().plusHours(24);
    }
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
