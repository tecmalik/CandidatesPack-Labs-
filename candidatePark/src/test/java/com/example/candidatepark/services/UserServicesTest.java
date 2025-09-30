package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServicesTest {
    @Autowired
    private UserServices userServices;

    @Test
    public void userCanSignUpTest(){
        User testUser = new User();
        testUser.setEmail(testMail2@gmail.com);
        userServices.signUp(testUser);
    }

}
