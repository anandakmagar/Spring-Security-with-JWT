package com.security.spring_security.dto;

public class OurUserDTO {
    private Long userId;          // User ID
    private String username;  // Username
    private String roles;     // Roles (e.g., "ADMIN,USER")

    // Constructors
    public OurUserDTO() {}

    public OurUserDTO(Long userId, String username, String roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}

