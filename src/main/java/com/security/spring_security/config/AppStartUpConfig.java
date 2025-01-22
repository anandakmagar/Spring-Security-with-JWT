package com.security.spring_security.config;

import com.security.spring_security.service.UserManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    The AppStartUpConfig class is a Spring configuration class that initializes an admin user during application startup.
    Using the @Configuration annotation, it defines a CommandLineRunner bean with the @Bean annotation, which executes logic after the application context is initialized.
    The admin user details, such as email, password, and role, are injected from the application.properties file using @Value annotations.
    The CommandLineRunner checks if the user database is empty by calling the isUserDatabaseEmpty() method in the UserManagementService.
    If no users exist, it creates an admin user with the injected credentials using the createAdminUserIfNotExists method.
    This ensures the application has a secure admin user available immediately after startup, preventing unauthorized access or an empty user database scenario.
 */

@Configuration // Marks this class as a source of bean definitions for the Spring container
public class AppStartUpConfig {

    @Value("${spring.security.user.email}") // Injects the admin email value from the application properties
    private String adminEmail;

    @Value("${spring.security.user.password}") // Injects the admin password value from the application properties
    private String adminPassword;

    @Value("${spring.security.user.role}") // Injects the admin role value from the application properties
    private String adminRole;

    @Bean // Defines a bean that will run at application startup
    public CommandLineRunner initAdminUser(UserManagementService userManagementService) {
        return args -> {
            if (userManagementService.isUserDatabaseEmpty()) // Checks if the user database is empty
                userManagementService.createAdminUserIfNotExists(adminEmail, adminPassword, adminRole); // Creates an admin user if no users exist
        };
    }
}
