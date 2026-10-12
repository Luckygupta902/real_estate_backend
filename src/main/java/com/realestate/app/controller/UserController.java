package com.realestate.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.app.request.LoginRequest;
import com.realestate.app.request.UserRequest;
import com.realestate.app.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserService userService;

    // Same as createUser, but for HTML forms (application/x-www-form-urlencoded)
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> createUserForm(@ModelAttribute UserRequest userRequest) {
        return createUser(userRequest);
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) {
        try {
            userRequest.validate();
            String result = userService.userRegistrationService(userRequest);
            return ResponseEntity.ok(result);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }

    }

    // Same as login, but for HTML forms (application/x-www-form-urlencoded)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@ModelAttribute LoginRequest loginRequest) {
        return login(loginRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {

        String token = userService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword());

        if (token.equals("Email not found") ||
                token.equals("Invalid password") ||
                token.equals("Please activate your account first")) {

            return ResponseEntity
                    .badRequest()
                    .body(token);
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "token", token));
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activatedAccount(@RequestParam String email) {

        String result = userService.activateAccount(email);

        return ResponseEntity.ok(result);
    }

}
