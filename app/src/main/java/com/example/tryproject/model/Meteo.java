package com.example.tryproject.model;

public class Meteo {
    public String ville;
    public double temperature;
    public double temperatureMin;
    public double temperatureMax;
    public String description;
    public int humidite;
    public double vitesseVent;
    public String alerte; // null si pas d'alerte

    public Meteo(String ville, double temperature, double temperatureMin,
                 double temperatureMax, String description,
                 int humidite, double vitesseVent) {
        this.ville = ville;
        this.temperature = temperature;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.description = description;
        this.humidite = humidite;
        this.vitesseVent = vitesseVent;
        this.alerte = genererAlerte();
    }

    // Génère une alerte si conditions dangereuses
    private String genererAlerte() {
        if (temperature > 38) {
            return "Alerte canicule ! Évitez les travaux entre 11h et 16h.";
        } else if (vitesseVent > 40) {
            return "Vent fort ! Protégez les jeunes plants.";
        } else if (humidite > 85) {
            return "Humidité élevée — risque de maladies fongiques.";
        }
        return null;
    }
}