package com.example.candidatepark.services;

import com.example.candidatepark.dtos.SignUpResponse;
import com.example.candidatepark.dtos.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class UserServicesTest {
    @Autowired
    private UserServices userServices;

    @Test
    public void userCanSignUpTest(){
        UserDTO testUser = new UserDTO();
        testUser.setEmail("testMail2@gmail.com");
        testUser.setPassword("testPassword");
        SignUpResponse signUpResponse = userServices.signUp(testUser);
        assertThat(signUpResponse).isNotNull();

    }
    @Test
    public void userCanNotSignInWithInvalidDetailsTest(){
        
    }

}
