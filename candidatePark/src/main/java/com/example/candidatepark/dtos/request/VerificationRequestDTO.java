package com.example.candidatepark.dtos.request;

import com.example.candidatepark.data.models.EmailVerificationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VerificationRequestDTO {
    private EmailVerificationStatus verificationStatus;
}
