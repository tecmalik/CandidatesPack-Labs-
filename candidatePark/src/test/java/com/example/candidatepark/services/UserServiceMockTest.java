package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import com.example.candidatepark.data.repository.TokenRepository;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.SignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private JWTService jwtService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userServices;

    UserDTO testUserDTO;
    @BeforeEach
    public void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setEmail("testMail2@gmail.com");
        testUserDTO.setPassword("testPassword");


    }
    @Test
    void userCanSignUpTest(){
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(testUserDTO.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(testUserDTO.getEmail())).thenReturn("mockJwtToken");

        User savedUser = new User();
        savedUser.setEmail(testUserDTO.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        SignUpResponse signUpResponse = userServices.signUp(testUserDTO);
        assertThat(signUpResponse.getMessage()).isNotNull();
        assertThat(signUpResponse.getToken()).isNotNull();
        assertThat(signUpResponse.getUser().getEmail()).isEqualTo(testUserDTO.getEmail());
        assertThat(signUpResponse.getEmailVerificationStatus()).isNotNull();
        assertEquals(VerificationStatus.PENDING, signUpResponse.getEmailVerificationStatus());

    }
    @Test
    void signupWithExistingEmailThrowsEmailInUseErrorTest(){


    }

}

