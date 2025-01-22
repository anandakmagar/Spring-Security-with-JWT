package com.security.spring_security.repository;

import com.security.spring_security.entity.OurUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Indicates that this interface is a Spring Data repository, enabling CRUD and query operations
public interface OurUserRepository extends JpaRepository<OurUser, Long> {

    // Method to find a user by their username
    Optional<OurUser> findByUsername(String username);

    // Method to find a user by their unique userId
    Optional<OurUser> findByUserId(Long userId);

    // Method to delete a user by their unique userId
    void deleteByUserId(Long userId);

    // Method to check if a user exists by their username
    boolean existsByUsername(String username);
}
