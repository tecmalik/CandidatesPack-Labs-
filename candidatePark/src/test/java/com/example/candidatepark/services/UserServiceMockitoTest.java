package com.example.candidatepark.services;

import com.example.candidatepark.data.models.LoginRateLimiter;
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
import com.example.candidatepark.exceptions.*;
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
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockitoTest {

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
    @Mock
    private LoginRateLimiter loginRateLimiter;
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

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getEmail(),
                        testUserDTO.getPassword(),
                        Collections.emptyList()
                );

        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(true);
        when(jwtService.generateToken(testUserDTO.getEmail())).thenReturn("mockJwtToken");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        LoginResponse loginResponse = userServices.login(testUserDTO);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getToken()).isNotNull();


    }
    @Test
    void userTryingToLoginWithUnVerifiedEmailRaiseErrorTest(){

        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail(testUserDTO.getEmail());
        loginDTO.setPassword("encodedPassword");

        User existingUser = new User();
        existingUser.setEmail(loginDTO.getEmail());
        existingUser.setPassword(loginDTO.getPassword());

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getEmail(),
                        testUserDTO.getPassword(),
                        Collections.emptyList()
                );
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        assertThrows(VerificationValidationException.class,()-> userServices.login(testUserDTO));



    }
    @Test
    void userLoginWithInvalidLoginThrowsExceptionTest(){
        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail("incorrectEmail");
        loginDTO.setPassword("encodedPassword");

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getEmail(),
                        testUserDTO.getPassword()
                );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(true);

        assertThrows(InvalidDetailsException.class,()->userServices.login(testUserDTO));


    }
    @Test
    void userLoginWithBlankPasswordThrowsExceptionTest() {
        testUserDTO.setPassword("");

        InvalidDetailsException exception = assertThrows(
                InvalidDetailsException.class,
                () -> userServices.login(testUserDTO)
        );

        assertThat(exception.getMessage()).contains("Details Can not Be Blank");
    }

    @Test
    void rateLimitIsCheckedBeforeAuthenticationTest() {
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(false);

        assertThrows(RateLimitExceededException.class, () -> userServices.login(testUserDTO));

        verify(loginRateLimiter).allowRequest(testUserDTO.getEmail());
        verify(authenticationManager, never()).authenticate(any());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void exceedingRateLimitThrowsRateLimitExceptionTest() {
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(false);

        RateLimitExceededException exception = assertThrows(
                RateLimitExceededException.class,
                () -> userServices.login(testUserDTO)
        );

        assertThat(exception.getMessage()).contains("Too many login attempts");
        assertThat(exception.getRetryAfterSeconds()).isEqualTo(300);
    }

    @Test
    void successfulLoginResetsRateLimitTest() {
        User existingUser = new User();
        existingUser.setEmail(testUserDTO.getEmail());
        existingUser.setPassword(testUserDTO.getPassword());
        existingUser.setEmailVerified(true);

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getEmail(),
                        testUserDTO.getPassword(),
                        Collections.emptyList()
                );

        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(jwtService.generateToken(testUserDTO.getEmail())).thenReturn("mockJwtToken");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        userServices.login(testUserDTO);

        verify(loginRateLimiter).resetLimit(testUserDTO.getEmail());
    }

    @Test
    void failedLoginDoesNotResetRateLimitTest() {
        User existingUser = new User();
        existingUser.setEmail(testUserDTO.getEmail());
        existingUser.setPassword(testUserDTO.getPassword());
        existingUser.setEmailVerified(false);

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        testUserDTO.getEmail(),
                        testUserDTO.getPassword(),
                        Collections.emptyList()
                );

        when(loginRateLimiter.allowRequest(testUserDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(testUserDTO.getEmail())).thenReturn(existingUser);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        assertThrows(VerificationValidationException.class, () -> userServices.login(testUserDTO));

        verify(loginRateLimiter).allowRequest(testUserDTO.getEmail());
        verify(loginRateLimiter, never()).resetLimit(testUserDTO.getEmail());
    }

    @Test
    void multipleFailedLoginAttemptsConsumesRateLimitTest() {
        User existingUser = new User();
        existingUser.setEmail(testUserDTO.getEmail());
        existingUser.setPassword("wrongPassword");
        existingUser.setEmailVerified(true);

        // Simulate 5 failed attempts
        when(loginRateLimiter.allowRequest(testUserDTO.getEmail()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false); // 6th attempt blocked

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new InvalidDetailsException("INVALID CREDENTIALS"));

        // First 5 attempts should throw InvalidDetailsException
        for (int i = 0; i < 5; i++) {
            assertThrows(InvalidDetailsException.class, () -> userServices.login(testUserDTO));
        }

        // 6th attempt should throw RateLimitExceededException
        assertThrows(RateLimitExceededException.class, () -> userServices.login(testUserDTO));

        verify(loginRateLimiter, times(6)).allowRequest(testUserDTO.getEmail());
        verify(loginRateLimiter, never()).resetLimit(testUserDTO.getEmail());
    }
}

