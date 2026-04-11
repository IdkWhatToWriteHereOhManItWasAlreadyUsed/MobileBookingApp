package com.example.mobilebookingapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String avatarUrl;
    private long createdAt;

    public User() {}

    public User(String userId, String name, String email, String avatarUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}