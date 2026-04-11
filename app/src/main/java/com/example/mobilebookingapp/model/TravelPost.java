package com.example.mobilebookingapp.model;

import java.io.Serializable;
import java.util.Date;

public class TravelPost implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private String text;
    private String imageUrl;
    private Date createdAt;

    public TravelPost() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatarUrl() { return userAvatarUrl; }
    public void setUserAvatarUrl(String userAvatarUrl) { this.userAvatarUrl = userAvatarUrl; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}