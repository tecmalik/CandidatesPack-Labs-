package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import com.example.candidatepark.data.models.VerificationToken;
import com.example.candidatepark.data.repository.TokenRepository;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.request.TokenDTO;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.LoginResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


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
    @Mock
    private AuthenticationManager authenticationManager;
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
        tokenDTO.setToken("mockJwtToken");
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setToken(tokenDTO.getToken());
        token.setExpiryDate(LocalDateTime.now().plusHours(1));


        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));

        VerificationResponseDTO verificationResponseDTO = userServices.verifyEmail(tokenDTO);
        assertThat(verificationResponseDTO).isNotNull();
        assertEquals(VerificationStatus.VERIFIED,verificationResponseDTO.getStatus());

    }
    @Test
    void userVerificationThrowsTokenExpiredExceptionWithExpiredTokenTest(){
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken("mockJwtToken");
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setToken(tokenDTO.getToken());
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));
        assertThrows(InvalidTokenException.class,()-> userServices.verifyEmail(tokenDTO));

    }
    @Test
    void userVerificationTrowsTokenExceptionWithUsedTokenTest (){
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken("mockJwtToken");
        VerificationToken token = new VerificationToken();
        User user = new User();
        user.setEmail(testUserDTO.getEmail());
        user.setPassword("encodedPassword");
        token.setUser(user);
        token.setUsed(true);
        token.setToken(tokenDTO.getToken());
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByToken(tokenDTO.getToken())).thenReturn(Optional.of(token));
        assertThrows(InvalidTokenException.class,()-> userServices.verifyEmail(tokenDTO));

    }
    @Test
    void userVerificationThrowsTokenExpiredExceptionWithInvalidTokenTest(){
        TokenDTO invalidTokenDTO = new TokenDTO();
        invalidTokenDTO.setToken("invalidMockJwtToken");

        when(tokenRepository.findByToken(invalidTokenDTO.getToken())).thenReturn(null);
        assertThrows(InvalidTokenException.class,()-> userServices.verifyEmail(invalidTokenDTO));

    }
    @Test
    void userCanLoginWithVarifiedEmailTest(){
        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail(testUserDTO.getEmail());
        loginDTO.setPassword("encodedPassword");


        User existingUser = new User();
        existingUser.setEmail(loginDTO.getEmail());
        existingUser.setPassword(loginDTO.getPassword());
        existingUser.setEmailVerified(true);

        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(jwtService.generateToken(testUserDTO.getEmail())).thenReturn("mockJwtToken");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(null);
        LoginResponse loginResponse = userServices.login(testUserDTO);

        assertThat(loginResponse).isNotNull();

    }




}

