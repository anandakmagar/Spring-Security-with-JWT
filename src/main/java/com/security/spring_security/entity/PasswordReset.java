package com.security.spring_security.entity;

import jakarta.persistence.*;

@Entity
@Table
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private long resetCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getResetCode() {
        return resetCode;
    }

    public void setResetCode(long resetCode) {
        this.resetCode = resetCode;
    }
}
