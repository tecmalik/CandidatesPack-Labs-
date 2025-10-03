package com.example.candidatepark.services;

import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.request.LoginResponse;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.exceptions.DuplicateSignUpException;
import com.example.candidatepark.exceptions.InvalidDetailsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class UserServicesTest {
    @Autowired
    private UserServices userServices;
    @Autowired
    private UserRepository userRepository;
    UserDTO testUserDTO;
    @BeforeEach
    public void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setEmail("testMail2@gmail.com");
        testUserDTO.setPassword("testPassword");
    }


    @Test
    public void userCanSignUpTest(){
        SignUpResponse signUpResponse = userServices.signUp(testUserDTO);
        assertThat(signUpResponse).isNotNull();
        userRepository.delete(userRepository.findByEmail(testUserDTO.getEmail()));
    }
    @Test
    public void userCanNotSignInWithInvalidDetailsTest(){
        testUserDTO.setEmail(" ");
        testUserDTO.setPassword("");
        assertThrows( InvalidDetailsException.class, ()-> userServices.signUp(testUserDTO));
    }
    @Test
    public void duplicateSignUpRaiseExceptionTest(){
        SignUpResponse signUpResponse = userServices.signUp(testUserDTO);
        assertThat(signUpResponse).isNotNull();
        UserDTO testUserDuplicate = testUserDTO;
        assertThrows(DuplicateSignUpException.class,()-> userServices.signUp(testUserDuplicate));
        userRepository.delete(userRepository.findByEmail(testUserDTO.getEmail()));
    }
    @Test
    public void userEmailCanBeVerifiedTest(){
//        VerificationRequestDTO verificationDTO = userServices.verifyEmail(testUser);

    }
    @Test
    public void userCanLoginTest(){
        LoginResponse loginResponse = userServices.login(testUserDTO);
        assertThat(loginResponse).isNotNull();

    }


}
