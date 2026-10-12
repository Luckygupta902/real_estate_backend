package com.realestate.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.app.configuration.EmailService;
import com.realestate.app.configuration.JwtService;
import com.realestate.app.entity.User;
import com.realestate.app.repository.UserRepository;
import com.realestate.app.request.UserRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtService jwtService;

    public String userRegistrationService(UserRequest userRequest) {
        // Check email
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check phone
        if (userRepository.existsByPhone(userRequest.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = new User();

        user.setName(userRequest.getName());
        // Self-registration always creates a regular USER.
        // ADMIN / AGENT roles must be assigned by an admin through a separate flow.
        user.setRoleId(userRequest.getRoleId());
        user.setEmail(userRequest.getEmail());
        user.setPassword(encryptPassword(userRequest.getPassword()));
        user.setPhone(userRequest.getPhone());
        user.setProfileImage(userRequest.getProfileImage());
        user.setCity(userRequest.getCity());
        user.setState(userRequest.getState());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        emailService.sendActivationEmail(userRequest.getEmail());
        return "User registered successfully";

    }

    public String encryptPassword(String password) {

        return passwordEncoder.encode(password);
    }

    public String login(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "Email not found";
        }

        User user = userOptional.get();

        boolean passwordMatch = passwordEncoder.matches(
                password,
                user.getPassword());

        if (!passwordMatch) {
            return "Invalid password";
        }

        if (!user.isActive()) {
            return "Please activate your account first";
        }

        String token = jwtService.generateToken(user);
        System.out.println(user.getRoleId()+"role id");

        return token;
    }

    public String activateAccount(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        user.setActive(true);

        userRepository.save(user);

        return "Account activated successfully";
    }

    // =========================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

// =========================
    // UPDATE USER
    // =========================

    public User updateUser(
            Long id,
            User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"
                    )
                );

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setRoleId(updatedUser.getRoleId());

        // Only change password if provided
        if (updatedUser.getPassword() != null &&
            !updatedUser.getPassword().isEmpty()) {

            user.setPassword(
                passwordEncoder.encode(
                    updatedUser.getPassword()
                )
            );
        }

        return userRepository.save(user);
    }


    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {

            throw new RuntimeException(
                "User not found"
            );
        }

        userRepository.deleteById(id);
    }

    public @Nullable Object getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }




    
    
}
