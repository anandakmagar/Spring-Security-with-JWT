package com.security.spring_security.dto;

public class PasswordResetRequest {
    private String username;
    private long resetCode;
    private String newPassword;

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
