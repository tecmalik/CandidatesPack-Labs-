package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.models.VerificationStatus;
import com.example.candidatepark.data.models.VerificationToken;
import com.example.candidatepark.data.repository.TokenRepository;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.response.LoginResponse;
import com.example.candidatepark.dtos.request.TokenDTO;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.VerificationResponseDTO;
import com.example.candidatepark.exceptions.DuplicateSignUpException;
import com.example.candidatepark.exceptions.InvalidDetailsException;
import com.example.candidatepark.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserServices{
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EmailService emailService;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    @Autowired
    private TokenRepository tokenRepository;


    @Override
    public SignUpResponse signUp(UserDTO testUser) {
        validateDetails(testUser);
        if (userExist(testUser)){
            SignUpResponse signUpResponse = new SignUpResponse();
            signUpResponse.setMessage("VERIFICATION RESENT");
            signUpResponse.setToken(jwtService.generateToken(testUser.getEmail()));
            signUpResponse.setEmailVerificationStatus(VerificationStatus.PENDING);
            return signUpResponse;
        }
        User user = new User();
        user.setEmail(testUser.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(testUser.getPassword()));
        User savedUser = userRepository.save(user);
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setMessage("Signup successful. Please verify your email.");
        signUpResponse.setToken(jwtService.generateToken(testUser.getEmail()));
        signUpResponse.setUser(savedUser);
        signUpResponse.setEmailVerificationStatus(VerificationStatus.PENDING);
    return signUpResponse;
    }


    @Override
    public LoginResponse login(UserDTO testUser) {
        validateDetails(testUser);
        verifyUser(testUser);

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setToken(jwtService.generateToken(testUser.getEmail()));
        return loginResponse;
    }

    @Override
    public VerificationResponseDTO verifyEmail(TokenDTO tokenDTO) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(tokenDTO.getToken());

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired())throw new InvalidTokenException("TOKEN_INVALID.");
        User user = tokenOpt.get().getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(tokenOpt.get());
        return VerificationResponseDTO
                .builder()
                .status(VerificationStatus.VERIFIED)
                .build();
    }


    private void verifyUser(UserDTO testUser) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(testUser.getEmail(),testUser.getPassword()));
        if (!authentication.isAuthenticated()) throw new InvalidDetailsException("INVALID CREDENTIALS");
    }

    private boolean userExist(UserDTO testUser) {
        User foundUser = userRepository.findByEmail(testUser.getEmail());
        if(foundUser != null && foundUser.isEmailVerified()) throw new DuplicateSignUpException("EMAIL IN USE");
        return foundUser != null;
    }

    private void sendEmailVerification(User foundUser) {
        String token = generatedToken(foundUser);
        emailService.sendVerificationEmail(token, foundUser.getEmail());
    }

    private String generatedToken(User foundUser) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(foundUser);
        tokenRepository.save(verificationToken);
        return token;
    }

    private void validateDetails(UserDTO testUser) {
        if(testUser.getEmail() == null || testUser.getEmail().isEmpty()) throw new InvalidDetailsException("Details Can not Be Blank");
        if(testUser.getPassword() == null || testUser.getPassword().isEmpty()) throw new InvalidDetailsException("Details Can not Be Blank");
    }



}
