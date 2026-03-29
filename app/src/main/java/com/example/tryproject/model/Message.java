package com.example.tryproject.model;

public class Message {

    public static final String AUTEUR_USER = "user";
    public static final String AUTEUR_BOT = "assistant";

    public String texte;
    public String auteur; // "user" ou "assistant"

    public Message(String texte, String auteur) {
        this.texte = texte;
        this.auteur = auteur;
    }
}
