package com.nurtureshare.app.network.model.response;

public class AuthResponse {
    private String token;
    private String userId;
    private String email;
    private String name;
    private String role;

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
