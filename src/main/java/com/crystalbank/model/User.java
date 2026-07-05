package com.crystalbank.model;

public class User {
    private int userId;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role; // "ADMIN" or "CLIENT"
    private String createdAt;

    public User() {}

    public User(int userId, String fullName, String username, String password,
                String email, String phone, String role, String createdAt) {
        this.userId    = userId;
        this.fullName  = fullName;
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.phone     = phone;
        this.role      = role;
        this.createdAt = createdAt;
    }

    public int getUserId()       { return userId; }
    public String getFullName()  { return fullName; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getEmail()     { return email; }
    public String getPhone()     { return phone; }
    public String getRole()      { return role; }
    public String getCreatedAt() { return createdAt; }

    public void setUserId(int userId)       { this.userId = userId; }
    public void setFullName(String fullName){ this.fullName = fullName; }
    public void setUsername(String username){ this.username = username; }
    public void setPassword(String password){ this.password = password; }
    public void setEmail(String email)      { this.email = email; }
    public void setPhone(String phone)      { this.phone = phone; }
    public void setRole(String role)        { this.role = role; }
    public void setCreatedAt(String createdAt){ this.createdAt = createdAt; }
}
