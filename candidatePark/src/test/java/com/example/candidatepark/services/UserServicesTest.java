package com.example.candidatepark.services;

import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.SignUpResponse;
import com.example.candidatepark.dtos.UserDTO;
import com.example.candidatepark.dtos.VerificationDTO;
import com.example.candidatepark.exceptions.DuplicateUserException;
import com.example.candidatepark.exceptions.InvalidDetailsException;
import org.junit.jupiter.api.AfterEach;
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
    UserDTO testUser;
    @BeforeEach
    public void setUp() {
        testUser = new UserDTO();
        testUser.setEmail("testMail2@gmail.com");
        testUser.setPassword("testPassword");
    }


    @Test
    public void userCanSignUpTest(){
        SignUpResponse signUpResponse = userServices.signUp(testUser);
        assertThat(signUpResponse).isNotNull();
        userRepository.delete(userRepository.findByEmail(testUser.getEmail()));
    }
    @Test
    public void userCanNotSignInWithInvalidDetailsTest(){
        testUser.setEmail(" ");
        testUser.setPassword("");
        assertThrows( InvalidDetailsException.class, ()-> userServices.signUp(testUser));
    }
    @Test
    public void duplicateSignUpRaiseExceptionTest(){
        SignUpResponse signUpResponse = userServices.signUp(testUser);
        assertThat(signUpResponse).isNotNull();
        UserDTO testUserDuplicate = testUser;
        assertThrows(DuplicateUserException.class,()-> userServices.signUp(testUserDuplicate));
        userRepository.delete(userRepository.findByEmail(testUser.getEmail()));
    }
//    @Test
//    public void userEmailCanBeVerifiedTest(){
//        VerificationDTO verificationDTO = userServices.verifyEmail(testUser);
//
//    }


}
