package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import com.example.candidatepark.data.models.VerificationToken;
import com.example.candidatepark.data.repository.TokenRepository;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.request.TokenDTO;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.response.VerificationResponseDTO;
import com.example.candidatepark.exceptions.DuplicateSignUpException;
import com.example.candidatepark.exceptions.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void signupWithExistingEmailThrowsEmailInUseErrorForVarifiedMailTest(){
        User existingUser = new User();
        existingUser.setEmailVerified(true);
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        assertThrows(DuplicateSignUpException.class, () -> userServices.signUp(testUserDTO));
    }
    @Test
    void signupWithExistingUnVerifiedEmailSendVerify(){

        User existingUser = new User();
        existingUser.setEmail(testUserDTO.getEmail());
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(jwtService.generateToken(testUserDTO.getEmail())).thenReturn("mockJwtToken");

        SignUpResponse signUpResponse = userServices.signUp(testUserDTO);
        assertEquals(testUserDTO.getEmail(),signUpResponse.getUser().getEmail());
        assertEquals("VERIFICATION RESENT",signUpResponse.getMessage());
        assertThat(signUpResponse.getToken()).isNotNull();
        assertEquals(VerificationStatus.PENDING, signUpResponse.getEmailVerificationStatus());
        
    }
    @Test
    void userEmailCanBeVerifiedWithValidTokenTest(){
        TokenDTO tokenDTO = new TokenDTO();
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setToken("mockJwtToken");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));


        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));

        VerificationResponseDTO verificationResponseDTO = userServices.verifyEmail(tokenDTO);
        assertThat(verificationResponseDTO).isNotNull();
        assertEquals(VerificationStatus.VERIFIED,verificationResponseDTO.getStatus());

    }
    @Test
    void userVerificationThrowsTokenExpiredExceptionWithInvalidTokenTest(){
        TokenDTO tokenDTO = new TokenDTO();
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setToken("mockJwtToken");
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));
        assertThrows(InvalidTokenException.class,()-> userServices.verifyEmail(tokenDTO));

    }
    @Test
    void userVerificationTrowsTokenExceptionWithUsedTokenTest (){
        TokenDTO tokenDTO = new TokenDTO();
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setUsed(true);
        token.setToken("mockJwtToken");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));
        assertThrows(InvalidTokenException.class,()-> userServices.verifyEmail(tokenDTO));

    }
    



}

