package com.security.spring_security.mapper;

import com.security.spring_security.dto.OurUserDTO;
import com.security.spring_security.entity.OurUser;
import org.springframework.stereotype.Component;

@Component // Marks this class as a Spring component, allowing it to be autodetected and managed as a Spring bean
public class OurUserMapper {

    // Method to map 'OurUser' entity to 'OurUserDTO'
    public OurUserDTO toDTO(OurUser user) {
        if (user == null) { // Checks if the 'OurUser' object is null to avoid NullPointerException
            return null; // Returns null if the input user is null
        }

        // Creates and returns an instance of 'OurUserDTO' using values from the 'OurUser' entity
        return new OurUserDTO(
                user.getUserId() != null ? Long.valueOf(user.getUserId().toString()) : null, // Converts userId to Long if not null
                user.getUsername(), // Maps the username
                user.getRoles() // Maps the roles
        );
    }
}

