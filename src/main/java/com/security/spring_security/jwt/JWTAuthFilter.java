package com.security.spring_security.jwt;

import com.security.spring_security.service.OurUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
    The JWTAuthFilter class is a custom security filter that extends OncePerRequestFilter to validate incoming requests with a JSON Web Token (JWT).
    It intercepts every HTTP request and checks for the presence of an Authorization header, which typically contains the JWT prefixed by "Bearer".
    If the header is missing or blank, the filter skips further processing and allows the request to proceed.
    If the header is present, the JWT token is extracted by removing the "Bearer " prefix.
    The filter uses the JWTUtils class to extract the username from the token and validates the token's authenticity and expiration.
    If the username is successfully retrieved and the token is valid, the OurUserDetailsService is used to load the user details.
    A UsernamePasswordAuthenticationToken is created with the user's details and roles, and the SecurityContextHolder is updated to store the authentication.
    This ensures that Spring Security recognizes the user as authenticated for the current request.
    After processing the token, the filter passes the request to the next filter in the chain.
    This setup seamlessly integrates JWT authentication with Spring Security, allowing secure access to protected endpoints.
 */

@Component // Marks this class as a Spring-managed component, making it eligible for dependency injection
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;  // JWT utility class that validates and parses tokens
    private final OurUserDetailsService userDetailsService;  // Custom service that loads user details

    // Constructor injects JWTUtils and OurUserDetailsService dependencies
    public JWTAuthFilter(JWTUtils jwtUtils, OurUserDetailsService ourUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = ourUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");  // Retrieves the Authorization header from the request
        final String jwtToken;  // Stores the JWT token extracted from the header
        final String userEmail;  // Stores the email extracted from the JWT token

        // Checks if the Authorization header is missing or blank, then continues the filter chain
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extracts the JWT token from the Authorization header (removing the "Bearer " prefix)
        jwtToken = authHeader.substring(7);
        // Extracts the username (email) from the JWT token
        userEmail = jwtUtils.extractUsername(jwtToken);

        // If the userEmail exists and no authentication is set in the SecurityContext, proceed
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loads user details using the username extracted from the token
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validates the JWT token against the loaded user details
            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                // Creates a new empty SecurityContext for the user authentication
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                // Creates an authentication token for the user with their roles and credentials
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // Sets authentication details (like the IP address) from the request
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Sets the authentication token in the SecurityContext
                securityContext.setAuthentication(token);
                // Stores the updated SecurityContext in the SecurityContextHolder
                SecurityContextHolder.setContext(securityContext);
            }
        }
        // Proceeds with the filter chain
        filterChain.doFilter(request, response);
    }
}

