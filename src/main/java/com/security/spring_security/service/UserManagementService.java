package com.security.spring_security.service;

import com.security.spring_security.dto.*;
import com.security.spring_security.entity.OurUser;
import com.security.spring_security.exception.UserAlreadyExistWithUsernameException;
import com.security.spring_security.jwt.JWTUtils;
import com.security.spring_security.mapper.OurUserMapper;
import com.security.spring_security.repository.OurUserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service // Indicates that this class provides business logic and is a service component in the Spring context
public class UserManagementService {
    // Provides access to user data
    private final OurUserRepository userRepository;
    // Handles JWT token generation and validation
    private final JWTUtils jwtUtils;
    // Manages authentication logic
    private final AuthenticationManager authenticationManager;
    // Encodes passwords for secure storage
    private final PasswordEncoder passwordEncoder;
    // Maps user entities to DTOs
    private final OurUserMapper ourUserMapper;

    // Constructor for injecting dependencies into the service
    @Autowired
    public UserManagementService(OurUserRepository userRepository,
                                 JWTUtils jwtUtils,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder, OurUserMapper ourUserMapper) {
        this.userRepository = userRepository; // Injects the user repository
        this.jwtUtils = jwtUtils; // Injects the JWT utility
        this.authenticationManager = authenticationManager; // Injects the authentication manager
        this.passwordEncoder = passwordEncoder; // Injects the password encoder
        this.ourUserMapper = ourUserMapper; // Injects the user mapper
    }

    // Checks whether the user database is empty by counting the total number of users
    public boolean isUserDatabaseEmpty() {
        return userRepository.count() == 0; // Returns true if no users exist, false otherwise
    }

    // Creates an admin user if one does not already exist in the database
    public void createAdminUserIfNotExists(String username, String password, String role) {
        if (userRepository.findByUsername(username).isEmpty()) { // Checks if the admin user exists
            OurUser user = new OurUser(); // Creates a new user instance
            user.setUsername(username); // Sets the admin username
            user.setPassword(passwordEncoder.encode(password)); // Encodes and sets the admin password
            user.setRoles(role); // Assigns the admin role
            userRepository.save(user); // Persists the admin user in the database
        }
    }

    // Generates a unique user ID using the current timestamp to ensure uniqueness
    private long generateUserId() {
        return System.currentTimeMillis(); // Uses the current time as a unique identifier
    }

    @Transactional // Ensures that this method's database operations are executed within a single transaction
    public boolean register(RegisterRequest registerRequest) {
        Optional<OurUser> user = userRepository.findByUsername(registerRequest.getUsername()); // Checks if the username is already registered
        if (user.isPresent()) {
            throw new UserAlreadyExistWithUsernameException("User already exists with email/username " + registerRequest.getUsername());
        } else {
            OurUser ourUser = new OurUser(); // Creates a new user entity
            ourUser.setUserId(generateUserId()); // Generates and sets a unique user ID
            ourUser.setUsername(registerRequest.getUsername()); // Sets the username
            ourUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encodes and sets the password
            ourUser.setRoles(registerRequest.getRole()); // Assigns the specified role
            userRepository.save(ourUser); // Saves the new user to the database
            return ourUser.getUserId() > 0; // Returns true if the user was successfully created
        }
    }

    // Authenticates the user and generates JWT tokens upon successful login
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            ); // Validates the user's credentials
            var user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(); // Retrieves the user entity
            var accessToken = jwtUtils.generateAccessToken(user); // Generates an access token
            var refreshToken = jwtUtils.generateRefreshToken(user); // Generates a refresh token

            AuthResponse authResponse = new AuthResponse(); // Creates a new authentication response object
            authResponse.setAccessToken(accessToken); // Sets the access token
            authResponse.setRefreshToken(refreshToken); // Sets the refresh token
            authResponse.setTokenType("Bearer"); // Specifies the token type
            authResponse.setExpiresIn(1800L); // Sets the token expiration time to 30 minutes

            return authResponse; // Returns the authentication response
        } catch (Exception e) {
            throw new RuntimeException("Login failed!"); // Throws an exception if authentication fails
        }
    }

    // Refreshes the user's JWT tokens using the provided refresh token
    public AuthResponse refreshToken(AuthResponse refreshTokenRequest) {
        try {
            String username = jwtUtils.extractUsername(refreshTokenRequest.getRefreshToken()); // Extracts the username from the token
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            String newAccessToken = jwtUtils.generateAccessToken(user); // Generates a new access token
            String newRefreshToken = jwtUtils.generateRefreshToken(user); // Optionally generates a new refresh token

            AuthResponse authResponse = new AuthResponse(); // Creates a new authentication response object
            authResponse.setAccessToken(newAccessToken); // Sets the new access token
            authResponse.setRefreshToken(newRefreshToken); // Sets the new refresh token
            authResponse.setTokenType("Bearer"); // Specifies the token type
            authResponse.setExpiresIn(3600L); // Sets the token expiration time to 60 minutes

            return authResponse; // Returns the authentication response
        } catch (Exception e) {
            throw new RuntimeException("Refresh token validation or regeneration failed!", e); // Throws an exception if token refresh fails
        }
    }

    @Transactional // Ensures that all updates to the user entity are performed within a single transaction
    public boolean updateUser(Long userId, OurUser updatedUser) {
        Optional<OurUser> userOptional = userRepository.findByUserId(userId); // Finds the user by ID
        if (userOptional.isPresent()) {
            OurUser existingUser = userOptional.get();

            if (updatedUser.getUsername() != null) {
                existingUser.setUsername(updatedUser.getUsername()); // Updates the username if provided
            }
            if (updatedUser.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Encodes and updates the password if provided
            }
            if (updatedUser.getRoles() != null) {
                existingUser.setRoles(updatedUser.getRoles()); // Updates the roles if provided
            }

            userRepository.save(existingUser); // Saves the updated user
            return true; // Returns true indicating success
        }
        return false; // Returns false if the user is not found
    }

    @Transactional // Ensures that the user deletion is performed within a single transaction
    public boolean deleteUser(Long userId) {
        Optional<OurUser> userOptional = userRepository.findByUserId(userId); // Finds the user by ID
        if (userOptional.isPresent()) {
            userRepository.deleteByUserId(userId); // Deletes the user
            return true; // Returns true indicating success
        }
        return false; // Returns false if the user is not found
    }

    @Transactional(readOnly = true) // Optimizes database interactions by ensuring only read operations occur
    public List<OurUserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(ourUserMapper::toDTO) // Maps each user entity to a DTO
                .collect(Collectors.toList()); // Collects and returns the list of user DTOs
    }
}
