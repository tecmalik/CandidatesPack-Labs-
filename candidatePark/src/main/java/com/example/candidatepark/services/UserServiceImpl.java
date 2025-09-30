package com.example.candidatepark.services;

import com.example.candidatepark.data.models.User;
import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.SignUpResponse;
import com.example.candidatepark.dtos.UserDTO;
import com.example.candidatepark.exceptions.DuplicateUserException;
import com.example.candidatepark.exceptions.InvalidDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServices{
    @Autowired
    UserRepository userRepository;


    @Override
    public SignUpResponse signUp(UserDTO testUser) {
        User user = new User();
        validateDetails(testUser);
        validateExistence(testUser);
        user.setEmail(testUser.getEmail());
        user.setPassword(testUser.getPassword());
        User savedUser = userRepository.save(user);
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setToken(savedUser.getId());
    return signUpResponse;
    }

    private void validateExistence(UserDTO testUser) {
        if(userRepository.findByEmail(testUser.getEmail())!=null) throw new DuplicateUserException("User already exists");
    }

    private void validateDetails(UserDTO testUser) {
        if(testUser.getEmail() == null || testUser.getEmail().isEmpty()) throw new InvalidDetailsException("Details Can not Be Blank");
        if(testUser.getPassword() == null || testUser.getPassword().isEmpty()) throw new InvalidDetailsException("Details Can not Be Blank");
    }

}
