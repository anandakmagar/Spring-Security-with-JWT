package com.security.spring_security.service;

import com.security.spring_security.entity.OurUser;
import com.security.spring_security.repository.OurUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    The OurUserDetailsService class is a custom implementation of Spring Security's UserDetailsService interface.
    It is marked with the @Service annotation to indicate that it is a Spring-managed service bean.
    This class is responsible for retrieving user details from the database when an authentication request is made.
    The primary purpose of this class is to load user information based on the provided username and provide it to Spring Security for authentication and authorization processes.
 */
@Service // Marks this class as a service component in the Spring context
public class OurUserDetailsService implements UserDetailsService {

    // Repository for accessing user data in the database
    private final OurUserRepository ourUserRepository;

    // Constructor for dependency injection of OurUserRepository
    @Autowired
    public OurUserDetailsService(OurUserRepository ourUserRepository) {
        this.ourUserRepository = ourUserRepository; // Initialize the repository with injected dependency
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetches the user from the repository by username
        // If the user is not found, throws a UsernameNotFoundException
        return ourUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found!") // Custom exception message for missing user
        );
    }
}
