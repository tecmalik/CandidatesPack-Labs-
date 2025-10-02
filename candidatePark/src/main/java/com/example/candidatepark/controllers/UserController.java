package com.example.candidatepark.controllers;


import com.example.candidatepark.data.repository.UserRepository;
import com.example.candidatepark.dtos.SignUpResponse;
import com.example.candidatepark.dtos.UserDTO;
import com.example.candidatepark.services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

;

@RestController
@RequestMapping("/V1/auth")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping("/")
    public String welcomeController(){
        return "HOMEPAGE FOR TALENT";
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserDTO userDTO){
        try {
            SignUpResponse signUpResponse = userServices.signUp(userDTO);
            return new ResponseEntity<>(signUpResponse , HttpStatus.OK);
        }catch(Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




}
