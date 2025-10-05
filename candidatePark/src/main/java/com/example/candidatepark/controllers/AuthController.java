package com.example.candidatepark.controllers;


import com.example.candidatepark.dtos.request.TokenDTO;
import com.example.candidatepark.dtos.response.LoginResponse;
import com.example.candidatepark.dtos.response.SignUpResponse;
import com.example.candidatepark.dtos.request.UserDTO;
import com.example.candidatepark.dtos.response.VerificationResponseDTO;
import com.example.candidatepark.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

;

@RestController
@RequestMapping("/V1/auth")
public class AuthController {

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

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestParam("token") TokenDTO tokenDTO){
        try{
            VerificationResponseDTO verificationResponseDTO = userServices.verifyEmail(tokenDTO);
            return new ResponseEntity<>(verificationResponseDTO, HttpStatus.OK);

        }catch(Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDTO userDTO){
        try {
            LoginResponse loginResponse = userServices.login(userDTO);
            return new ResponseEntity<>(loginResponse , HttpStatus.OK);
        }catch (Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
