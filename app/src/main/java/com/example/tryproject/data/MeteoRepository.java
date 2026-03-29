package com.example.tryproject.data;

import com.example.tryproject.model.Meteo;

public class MeteoRepository {

    public interface MeteoCallback {
        void onMeteo(Meteo meteo);
        void onErreur(String erreur);
    }

    public void getMeteo(String ville, MeteoCallback callback) {
        new Thread(() -> {
            try {
                Thread.sleep(800); // délai simulé
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Données simulées selon la ville
            Meteo meteo;
            String villeLower = ville.toLowerCase().trim();

            if (villeLower.contains("settat")) {
                meteo = new Meteo("Settat", 28, 18, 32,
                        "partiellement nuageux", 55, 15);

            } else if (villeLower.contains("casablanca") || villeLower.contains("casa")) {
                meteo = new Meteo("Casablanca", 22, 17, 25,
                        "ciel dégagé", 70, 20);

            } else if (villeLower.contains("marrakech")) {
                meteo = new Meteo("Marrakech", 36, 24, 40,
                        "ensoleillé", 30, 10);

            } else if (villeLower.contains("rabat")) {
                meteo = new Meteo("Rabat", 24, 18, 27,
                        "légèrement nuageux", 65, 18);

            } else if (villeLower.contains("fes") || villeLower.contains("fès")) {
                meteo = new Meteo("Fès", 30, 20, 34,
                        "ensoleillé", 40, 8);

            } else if (villeLower.contains("agadir")) {
                meteo = new Meteo("Agadir", 26, 20, 29,
                        "ciel dégagé", 60, 25);

            } else if (villeLower.contains("meknes") || villeLower.contains("meknès")) {
                meteo = new Meteo("Meknès", 29, 19, 33,
                        "partiellement nuageux", 45, 12);

            } else {
                // Ville non reconnue - données génériques
                meteo = new Meteo(ville, 25, 17, 30,
                        "ensoleillé", 50, 14);
            }

            callback.onMeteo(meteo);

        }).start();
    }
}