package com.example.livenet.model;

public class FireUser {
    private String id;
    private String image;
    private String username;
    private String status;


    public FireUser() {
    }

    public FireUser(String id, String image, String username) {
        this.id = id;
        this.username = username;
        this.image = image;
    }

    public FireUser(String id, String image, String username, String status) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
