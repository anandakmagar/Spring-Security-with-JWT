package com.security.spring_security.service;

import com.security.spring_security.dto.PasswordResetRequest;
import com.security.spring_security.entity.OurUser;
import com.security.spring_security.entity.PasswordReset;
import com.security.spring_security.mail.EmailService;
import com.security.spring_security.repository.OurUserRepository;
import com.security.spring_security.repository.PasswordResetCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service // Marks this class as a service component in the Spring context
public class PasswordResetCodeService {
    // Provides access to password reset code data
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    // Provides access to user data
    private final OurUserRepository ourUserRepository;
    // Handles password encoding
    private final PasswordEncoder passwordEncoder;
    // Handles email sending functionality
    private final EmailService emailService;

    // Initializes dependencies via constructor injection
    @Autowired
    public PasswordResetCodeService(PasswordResetCodeRepository passwordResetCodeRepository, OurUserRepository ourUserRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordResetCodeRepository = passwordResetCodeRepository; // Sets the repository for password reset codes
        this.ourUserRepository = ourUserRepository; // Sets the repository for user data
        this.passwordEncoder = passwordEncoder; // Sets the password encoder
        this.emailService = emailService; // Sets the email service
    }

    // Retrieves a password reset entry by username
    public PasswordReset findByUsername(String username) {
        return passwordResetCodeRepository.findByUsername(username); // Fetches the password reset entry
    }

    // Sends a password reset code to the user's email
    public boolean sendPasswordResetCode(String username) {
        // Checks if a password reset entry already exists
        PasswordReset existingPasswordReset = passwordResetCodeRepository.findByUsername(username);
        if (existingPasswordReset != null) {
            long existingPasswordResetCode = existingPasswordReset.getResetCode(); // Retrieves the existing reset code
            passwordResetCodeRepository.deleteByResetCode(existingPasswordResetCode); // Deletes the existing reset code
        }

        // Checks if the user exists in the user repository
        if (ourUserRepository.existsByUsername(username)) {
            // Generates a random password reset code
            long code = 1000000000L + new Random().nextInt(900000000);
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setResetCode(code); // Sets the reset code
            passwordReset.setUsername(username); // Sets the username

            // Saves the new password reset entry
            passwordResetCodeRepository.save(passwordReset);

            // Formats and sends the password reset email
            String message = String.format("%s, your password reset code is %d.", username, code);
            emailService.sendEmail(username, "Password Reset Code Delivery", message); // Sends the email
            return true; // Returns true indicating success
        } else {
            return false; // Returns false if the user does not exist
        }
    }

    // Changes the user's password using a reset code
    public boolean changePassword(PasswordResetRequest passwordResetRequest) {
        // Retrieves the user from the repository
        OurUser ourUser = ourUserRepository.findByUsername(passwordResetRequest.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("Username/Email not found!") // Throws an exception if the user is not found
        );

        // Retrieves the password reset entry from the repository
        PasswordReset passwordReset = passwordResetCodeRepository.findByUsername(passwordResetRequest.getUsername());

        // Validates the username and reset code
        if (passwordReset != null &&
                ourUser.getUsername().equals(passwordReset.getUsername()) &&
                passwordResetRequest.getResetCode() == passwordReset.getResetCode()) {

            // Encodes the new password
            String encodedPassword = passwordEncoder.encode(passwordResetRequest.getNewPassword());

            // Updates the user's password
            ourUser.setPassword(encodedPassword);
            ourUserRepository.save(ourUser); // Saves the updated user

            return true; // Returns true indicating success
        }

        // Returns false if validation fails
        return false;
    }
}
