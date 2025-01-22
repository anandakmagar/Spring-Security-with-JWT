package com.security.spring_security.repository;

import com.security.spring_security.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indicates that this interface is a Spring Data repository for managing PasswordReset entities
public interface PasswordResetCodeRepository extends JpaRepository<PasswordReset, Long> {

    // Method to find a PasswordReset entity by the associated username
    PasswordReset findByUsername(String username);

    // Method to find a PasswordReset entity by the reset code
    PasswordReset findByResetCode(long resetCode);

    // Method to delete a PasswordReset entity by the reset code
    void deleteByResetCode(long resetCode);
}

