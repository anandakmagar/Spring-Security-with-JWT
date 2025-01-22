package com.security.spring_security.controller;

import com.security.spring_security.dto.*;
import com.security.spring_security.entity.OurUser;
import com.security.spring_security.entity.PasswordReset;
import com.security.spring_security.repository.PasswordResetCodeRepository;
import com.security.spring_security.service.PasswordResetCodeService;
import com.security.spring_security.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a Spring REST controller
@RequestMapping("/api") // Base path for all endpoints in this controller
public class AuthController {

    private final UserManagementService userManagementService; // Service to manage user-related operations
    private final PasswordResetCodeService passwordResetCodeService; // Service to handle password reset operations

    @Autowired // Constructor-based dependency injection
    public AuthController(UserManagementService userManagementService, PasswordResetCodeService passwordResetCodeService) {
        this.userManagementService = userManagementService;
        this.passwordResetCodeService = passwordResetCodeService;
    }

    // Public endpoint for login
    @PostMapping("/auth/login") // Maps POST requests to /api/auth/login
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userManagementService.login(loginRequest); // Handles user login
        return ResponseEntity.ok(authResponse); // Returns the authentication response
    }

    // Public endpoint for user registration
    @PostMapping("/auth/register") // Maps POST requests to /api/auth/register
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        boolean isRegistered = userManagementService.register(registerRequest); // Registers a new user
        if (isRegistered) {
            return ResponseEntity.ok("User registered successfully!"); // Success response
        } else {
            return ResponseEntity.badRequest().body("Failed to register user."); // Failure response
        }
    }

    @GetMapping("/auth/send-reset-code/{username}") // Maps GET requests to /api/auth/send-reset-code/{username}
    public ResponseEntity<String> sendPasswordResetCode(@PathVariable String username) {
        boolean result = passwordResetCodeService.sendPasswordResetCode(username); // Sends a password reset code
        if (result) {
            return ResponseEntity.ok("Password reset code sent successfully."); // Success response
        } else {
            return ResponseEntity.badRequest().body("Failed to send password reset code."); // Failure response
        }
    }

    @PostMapping("/auth/change-password") // Maps POST requests to /api/auth/change-password
    public ResponseEntity<String> changePassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            boolean result = passwordResetCodeService.changePassword(passwordResetRequest); // Handles password change
            if (result) {
                return ResponseEntity.ok("Password changed successfully."); // Success response
            } else {
                return ResponseEntity.badRequest().body("Invalid reset code or email."); // Failure response for invalid inputs
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Username/email not found!!"); // Failure response for non-existent user
        }
    }

    // Update user information (ADMIN and USER)
    @PutMapping("/users/{userId}") // Maps PUT requests to /api/users/{userId}
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')") // Restricts access to ADMIN and USER roles
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody OurUser updatedUser) {
        boolean isUpdated = userManagementService.updateUser(userId, updatedUser); // Updates user information
        if (isUpdated) {
            return ResponseEntity.ok("User updated successfully!"); // Success response
        } else {
            return ResponseEntity.badRequest().body("Failed to update user."); // Failure response
        }
    }

    // Delete user (ADMIN only)
    @DeleteMapping("/users/{userId}") // Maps DELETE requests to /api/users/{userId}
    @PreAuthorize("hasRole('ADMIN')") // Restricts access to ADMIN role
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userManagementService.deleteUser(userId); // Deletes a user
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully!"); // Success response
        } else {
            return ResponseEntity.badRequest().body("Failed to delete user."); // Failure response
        }
    }

    // List all users (ADMIN only)
    @GetMapping("/users") // Maps GET requests to /api/users
    @PreAuthorize("hasRole('ADMIN')") // Restricts access to ADMIN role
    public ResponseEntity<List<OurUserDTO>> getAllUsers() {
        List<OurUserDTO> users = userManagementService.getAllUsers(); // Retrieves all users
        return ResponseEntity.ok(users); // Returns the list of users
    }
}
