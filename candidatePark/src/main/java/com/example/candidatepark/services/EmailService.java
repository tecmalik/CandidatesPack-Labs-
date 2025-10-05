package com.example.candidatepark.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Value("${app.environment:dev}")
    private String environment;
    @Autowired(required =false)
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = "http://localhost:8080/V1/auth/verify?token=" + token;

        if ("dev".equals(environment) || mailSender == null) {
            // Just log it for testing
            logger.info("===========================================");
            logger.info("VERIFICATION EMAIL (DEV MODE)");
            logger.info("To: {}", toEmail);
            logger.info("Verification URL: {}", verificationUrl);
            logger.info("===========================================");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify Your Email");
        message.setText("Click to verify: " + verificationUrl);

        mailSender.send(message);
    }

}
