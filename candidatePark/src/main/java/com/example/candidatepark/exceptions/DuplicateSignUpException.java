package com.example.candidatepark.exceptions;

public class DuplicateSignUpException extends RuntimeException {
    public DuplicateSignUpException(String message) {
        super(message);
    }
}
