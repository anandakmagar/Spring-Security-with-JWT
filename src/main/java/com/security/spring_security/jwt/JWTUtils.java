package com.security.spring_security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/*
    The JWTUtils class is a Spring-managed component that handles the creation, validation, and parsing of JWT tokens for secure authentication.
    It initializes a signing key using a secret string provided in the application properties and decodes it into a SecretKeySpec for HMAC SHA-256 signing.
    The class provides methods to generate access and refresh tokens, setting the username as the subject and defining expiration times (30 minutes for access tokens and 1 hour for refresh tokens).
    It includes utility methods like extractClaims for parsing claims from tokens and extractUsername for retrieving the username.
    Additionally, it validates tokens by checking their expiration and matching the username with the given UserDetails.
    These functionalities collectively enable robust JWT-based authentication in the application.


    A JWT typically consists of three parts: header, payload, signature

    1. Header: Contains metadata about the token, such as the type (JWT) and signing algorithm (HS256, RS256, etc.).
    2. Payload (Claims): This is where the actual data is stored. Claims are essentially key-value pairs that describe information about the token
       subject (e.g., the user's identity, roles, or permissions).
    3. Signature: A cryptographic signature used to verify the integrity of the token.
       For example, the payload of a JWT might look like this:

        {
          "sub": "username123",        // Subject (usually the username)
          "iat": 1674768000,           // Issued At (timestamp)
          "exp": 1674771600,           // Expiration Time (timestamp)
          "roles": "ROLE_USER"         // Custom claim (user roles)
        }
        What is Claims?
        The Claims object in the code is a part of the JJWT library (Java JWT library), and it represents the payload (decoded data) of the token.
        It provides methods to access the claims in the payload, such as the subject (sub), issue date (iat), expiration date (exp), or any custom claims that is included.
 */

@Component // Marks this class as a Spring-managed component, making it eligible for dependency injection
public class JWTUtils {
    // Secret key used for signing and verifying the JWT
    private final SecretKeySpec Key;

    // Constructor initializes the signing key using the secret from application properties
    public JWTUtils(@Value("${jwt.secret}") String secretString) {
        // Decodes the secret string and creates the signing key
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // Generates a JWT access token for the given UserDetails
    public String generateAccessToken(UserDetails userDetails) {
        // Builds the access token with the username as the subject, current time as issue date,
        // an expiration time of 30 minutes, and signs it using the key
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Sets the username as the token subject
                .issuedAt(new Date(System.currentTimeMillis())) // Sets issue date
                .expiration(new Date(System.currentTimeMillis() + 1800000)) // Sets expiration date (30 mins)
                .signWith(Key) // Signs the token with the HMAC key
                .compact(); // Returns the final token as a compact string
    }

    // Generates a JWT refresh token for the given UserDetails
    public String generateRefreshToken(UserDetails userDetails) {
        // Builds the refresh token with the username as the subject, current time as issue date,
        // an expiration time of 1 hour, and signs it using the key
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Sets the username as the token subject
                .issuedAt(new Date(System.currentTimeMillis())) // Sets issue date
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // Sets expiration date (1 hour)
                .signWith(Key) // Signs the token with the HMAC key
                .compact(); // Returns the final token as a compact string
    }

    // Extracts claims from a JWT token using a provided function
    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        // Parses the token, verifies its signature, and extracts claims using the provided function
        return claimsTFunction.apply(
                Jwts.parser()
                        .verifyWith(Key) // Verifies the token with the signing key
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
        );
    }

    // Extracts the username (subject) from a token
    public String extractUsername(String token) {
        // Uses the extractClaims method to get the "subject" claim
        return extractClaims(token, Claims::getSubject);
    }

    // Checks if the token is expired
    private boolean isTokenExpired(String token) {
        // Extracts the expiration claim and checks if it's before the current time
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    // Validates if the token is valid for the given UserDetails
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extracts the username from the token and ensures it matches the user's username
        // Also checks that the token is not expired
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
