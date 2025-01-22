package com.security.spring_security.dto;

public class AuthResponse {
    private String accessToken;           // The JWT access token
    private String refreshToken;          // The JWT refresh token
    private String tokenType = "Bearer";  // Token type (usually "Bearer")
    private Long expiresIn;               // Expiration time of the access token in seconds

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

