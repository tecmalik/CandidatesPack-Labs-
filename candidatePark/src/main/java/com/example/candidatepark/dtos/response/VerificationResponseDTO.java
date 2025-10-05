package com.example.candidatepark.dtos.response;

import com.example.candidatepark.data.models.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerificationResponseDTO {
    private VerificationStatus status;
}
