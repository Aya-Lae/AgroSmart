package com.example.tryproject.model;

public class FicheCulture {
    public String nom;
    public String emoji;
    public String description;
    public String periodesSemis;
    public String irrigation;
    public String fertilisation;
    public String maladiesFrequentes;
    public String conseilsRecolte;

    public FicheCulture(String nom, String emoji, String description,
                        String periodesSemis, String irrigation,
                        String fertilisation, String maladiesFrequentes,
                        String conseilsRecolte) {
        this.nom = nom;
        this.emoji = emoji;
        this.description = description;
        this.periodesSemis = periodesSemis;
        this.irrigation = irrigation;
        this.fertilisation = fertilisation;
        this.maladiesFrequentes = maladiesFrequentes;
        this.conseilsRecolte = conseilsRecolte;
    }
}