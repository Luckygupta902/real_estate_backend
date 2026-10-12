
package com.realestate.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.User;
import com.realestate.app.request.UserRequest;
import com.realestate.app.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // CREATE USER - ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(
            @RequestBody UserRequest userRequest) {

        try {
            userRequest.validate();

            String result =
                userService.userRegistrationService(userRequest);

            return ResponseEntity.ok(result);

        } catch (Exception exception) {
            return ResponseEntity.badRequest()
                .body(exception.getMessage());
        }
    }

    // GET ALL USERS - ADMIN ONLY
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(
            userService.getAllUsers()
        );
    }

    // GET USER BY ID - ADMIN ONLY
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getUser(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            userService.getUserById(id)
        );
    }

    // UPDATE USER - ADMIN ONLY
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return ResponseEntity.ok(
            userService.updateUser(id, user)
        );
    }

    // DELETE USER - ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
            "User deleted successfully"
        );
    }
}