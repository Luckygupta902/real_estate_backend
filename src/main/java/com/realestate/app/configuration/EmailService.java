package com.realestate.app.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendActivationEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Activate Your Account");

        message.setText(
                "Welcome to Real Estate App!\n\n" +
                        "Your account has been created successfully.\n\n" +
                        "Please click the link below to activate your account:\n\n" +
                        "http://localhost:8080/api/auth/activate?email=" + email + "\n\n" +
                        "Thank you!");

        mailSender.send(message);

        System.out.println("Activation email sent to: " + email);
    }
}
