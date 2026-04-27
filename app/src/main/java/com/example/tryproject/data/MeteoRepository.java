package com.example.tryproject.data;

import com.example.tryproject.model.Meteo;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MeteoRepository {

    // Remplace par ta clé OpenWeather
    private static final String API_KEY = "a2e2a080a3c4792893d7fc2ee1101bec";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public interface MeteoCallback {
        void onMeteo(Meteo meteo);
        void onErreur(String erreur);
    }

    public void getMeteo(String ville, MeteoCallback callback) {
        new Thread(() -> {
            try {
                // Construire l'URL de la requête
                String urlString = BASE_URL
                        + "?q=" + ville
                        + "&appid=" + API_KEY
                        + "&units=metric"      // température en Celsius
                        + "&lang=fr";          // description en français

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);

                // Lire la réponse
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // Parser le JSON reçu
                JSONObject json = new JSONObject(sb.toString());
                JSONObject main = json.getJSONObject("main");
                JSONObject wind = json.getJSONObject("wind");
                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

                Meteo meteo = new Meteo(
                        json.getString("name"),
                        main.getDouble("temp"),
                        main.getDouble("temp_min"),
                        main.getDouble("temp_max"),
                        weather.getString("description"),
                        main.getInt("humidity"),
                        wind.getDouble("speed") * 3.6 // m/s → km/h
                );

                callback.onMeteo(meteo);

            } catch (Exception e) {
                callback.onErreur("Impossible de récupérer la météo : " + e.getMessage());
            }
        }).start();
    }
}