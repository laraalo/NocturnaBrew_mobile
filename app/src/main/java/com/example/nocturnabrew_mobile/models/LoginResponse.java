package com.example.nocturnabrew_mobile.models;

public class LoginResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }
}
