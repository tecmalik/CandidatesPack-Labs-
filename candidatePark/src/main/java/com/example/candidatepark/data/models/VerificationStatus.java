package com.example.candidatepark.data.models;

public enum VerificationStatus {
    PENDING("Email Verification pending", "User registered but email not verified yet"),
    VERIFIED("Email verified", "User has successfully verified their email"),
    EXPIRED("Verification link expired", "Verification token has expired"),
    FAILED("Verification failed", "Email verification attempt failed");

    private final String status;
    private final String description;
    VerificationStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVerified() {
        return this == VERIFIED;
    }
}
