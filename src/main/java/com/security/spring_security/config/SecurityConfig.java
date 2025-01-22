package com.security.spring_security.config;

import com.security.spring_security.jwt.JWTAuthFilter;
import com.security.spring_security.service.OurUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/*
    The SecurityConfig class is a Spring Security configuration that defines how authentication and authorization are handled in a Spring Boot application.
    It is marked with the @Configuration and @EnableWebSecurity annotations to indicate that it provides security-related beans and enables web security features.
    The class uses constructor injection to initialize dependencies, such as JWTAuthFilter for processing JWT tokens and OurUserDetailsService for loading user details from the database.

    The core functionality is defined in the securityFilterChain method, which configures the security policies.
    This method starts by disabling CSRF protection, as this is a stateless API where CSRF tokens are unnecessary.
    It then enables CORS (Cross-Origin Resource Sharing), which allows requests from other domains.
    The authorization rules are specified next, allowing public access to endpoints such as login and specific API paths.

    Session management is configured as stateless to ensure that no session data is stored on the server, making it suitable for APIs.
    The method also integrates a custom authentication provider and adds the JWTAuthFilter before the UsernamePasswordAuthenticationFilter,
    allowing the application to validate and process JWT tokens.

    The authenticationProvider bean configures a DaoAuthenticationProvider, which links the OurUserDetailsService for fetching user data and a PasswordEncoder
    for securely hashing passwords. The passwordEncoder bean uses the bcrypt algorithm with a strength of 12 to ensure robust password security.
    Additionally, the authenticationManager bean exposes an AuthenticationManager, enabling programmatic authentication for login or other scenarios.

    Overall, this configuration ensures secure and efficient handling of authentication and authorization. By leveraging stateless JWT authentication,
    bcrypt for password hashing, and a clean separation of concerns, the application follows best practices for scalable and maintainable security in real-world applications.
 */

@Configuration // Marks this class as a source of bean definitions for the Spring container
@EnableWebSecurity // Enables Spring Security for the application
@EnableMethodSecurity // Enables method-level security annotations like @PreAuthorize
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter; // Custom JWT authentication filter
    private final OurUserDetailsService ourUserDetailsService; // Custom UserDetailsService implementation

    public SecurityConfig(JWTAuthFilter jwtAuthFilter, OurUserDetailsService ourUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter; // Injecting JWTAuthFilter
        this.ourUserDetailsService = ourUserDetailsService; // Injecting OurUserDetailsService
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection for stateless APIs

                .cors(Customizer.withDefaults()) // Enables CORS with default configuration

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",           // Login endpoint
                                "/api/auth/register",        // Registration endpoint
                                "/public/**",                // Any public resources
                                "/actuator/metrics/**",      // Actuator metrics for monitoring
                                "/targets",                  // Prometheus targets
                                "/actuator/prometheus",      // Prometheus metrics
                                "/api/auth/send-reset-code/**", // Password reset code
                                "/api/auth/change-password"  // Change password
                        ).permitAll() // Allows public access to the listed endpoints

                        .anyRequest().authenticated() // Requires authentication for all other endpoints
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configures session management as stateless for JWT-based authentication

                .authenticationProvider(authenticationProvider()) // Sets the custom authentication provider

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Adds JWT filter before the default UsernamePasswordAuthenticationFilter

        return httpSecurity.build(); // Builds and returns the SecurityFilterChain
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider(); // Creates a DAO-based authentication provider
        daoAuthProvider.setUserDetailsService(ourUserDetailsService); // Links the custom UserDetailsService
        daoAuthProvider.setPasswordEncoder(passwordEncoder()); // Sets the password encoder for hashing and validation
        return daoAuthProvider; // Returns the authentication provider
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Creates a BCrypt password encoder with strength 12
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Returns the AuthenticationManager configured by Spring Security
    }
}

