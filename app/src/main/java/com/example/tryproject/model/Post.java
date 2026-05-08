package com.example.tryproject.model;

public class Post {

    private String id;
    private String utilisateur;
    private String message;
    private String imageUrl;
    private long date;

    // Constructeur vide obligatoire pour Firebase
    public Post() {
    }

    public Post(String id,
                String utilisateur,
                String message,
                String imageUrl,
                long date) {

        this.id = id;
        this.utilisateur = utilisateur;
        this.message = message;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    // GETTERS

    public String getId() {
        return id;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getDate() {
        return date;
    }

    // SETTERS

    public void setId(String id) {
        this.id = id;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(long date) {
        this.date = date;
    }
}