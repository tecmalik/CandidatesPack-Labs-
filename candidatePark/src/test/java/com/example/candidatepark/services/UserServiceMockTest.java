package com.example.candidatepark.services;

import com.example.candidatepark.data.models.VerificationStatus;
import com.example.candidatepark.data.repository.TokenRepository;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.SignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @Mock
    private UserServices userServices;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    UserDTO testUserDTO;
    @BeforeEach
    public void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setEmail("testMail2@gmail.com");
        testUserDTO.setPassword("testPassword");
    }
    @Test
    void userCanSignUpTest(){
        SignUpResponse signUpResponse = userServices.signUp(testUserDTO);
        assertThat(signUpResponse.getToken()).isNotNull();
        assertThat(signUpResponse.getEmailVerificationStatus()).isNotNull();
        assertEquals(VerificationStatus.PENDING, signUpResponse.getEmailVerificationStatus());

        userRepository.delete(userRepository.findByEmail(testUserDTO.getEmail()));
    }

}
