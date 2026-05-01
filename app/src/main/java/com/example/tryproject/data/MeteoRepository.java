package com.example.tryproject.data;

import com.example.tryproject.model.Meteo;
import com.example.tryproject.model.Prevision;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeteoRepository {

    private static final String API_KEY = "a2e2a080a3c4792893d7fc2ee1101bec";
    private static final String URL_METEO = "https://api.openweathermap.org/data/2.5/weather";
    private static final String URL_PREVISIONS = "https://api.openweathermap.org/data/2.5/forecast";

    public interface MeteoCallback {
        void onMeteo(Meteo meteo);
        void onErreur(String erreur);
    }

    public interface PrevisionCallback {
        void onPrevisions(List<Prevision> previsions);
    }

    public void getMeteo(String ville, MeteoCallback callback) {
        new Thread(() -> {
            try {
                String urlString = URL_METEO + "?q=" + ville
                        + "&appid=" + API_KEY
                        + "&units=metric&lang=fr";

                String response = fetch(urlString);
                JSONObject json = new JSONObject(response);
                JSONObject main = json.getJSONObject("main");
                JSONObject wind = json.getJSONObject("wind");
                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                JSONObject sys = json.getJSONObject("sys");

                long sunsetTimestamp = sys.getLong("sunset") * 1000L;
                String sunset = new SimpleDateFormat("HH:mm", Locale.FRENCH)
                        .format(new Date(sunsetTimestamp));

                Meteo meteo = new Meteo(
                        json.getString("name"),
                        main.getDouble("temp"),
                        main.getDouble("temp_min"),
                        main.getDouble("temp_max"),
                        weather.getString("description"),
                        main.getInt("humidity"),
                        wind.getDouble("speed") * 3.6
                );
                meteo.sunset = sunset;
                meteo.iconeCode = weather.getString("icon");

                callback.onMeteo(meteo);

            } catch (Exception e) {
                callback.onErreur("Ville introuvable ou erreur réseau.");
            }
        }).start();
    }
    public void getMeteoParCoordonnees(double lat, double lon, MeteoCallback callback) {
        new Thread(() -> {
            try {
                String urlString = URL_METEO
                        + "?lat=" + lat
                        + "&lon=" + lon
                        + "&appid=" + API_KEY
                        + "&units=metric&lang=fr";

                String response = fetch(urlString);
                JSONObject json = new JSONObject(response);
                JSONObject main = json.getJSONObject("main");
                JSONObject wind = json.getJSONObject("wind");
                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                JSONObject sys = json.getJSONObject("sys");

                long sunsetTimestamp = sys.getLong("sunset") * 1000L;
                String sunset = new SimpleDateFormat("HH:mm", Locale.FRENCH)
                        .format(new Date(sunsetTimestamp));

                Meteo meteo = new Meteo(
                        json.getString("name"),
                        main.getDouble("temp"),
                        main.getDouble("temp_min"),
                        main.getDouble("temp_max"),
                        weather.getString("description"),
                        main.getInt("humidity"),
                        wind.getDouble("speed") * 3.6
                );
                meteo.sunset = sunset;
                meteo.iconeCode = weather.getString("icon");

                callback.onMeteo(meteo);

            } catch (Exception e) {
                callback.onErreur("Erreur GPS : " + e.getMessage());
            }
        }).start();
    }

    public void getPrevisionParCoordonnees(double lat, double lon,
                                           PrevisionCallback callback) {
        new Thread(() -> {
            try {
                String urlString = URL_PREVISIONS
                        + "?lat=" + lat
                        + "&lon=" + lon
                        + "&appid=" + API_KEY
                        + "&units=metric&lang=fr&cnt=48";

                String response = fetch(urlString);
                JSONObject json = new JSONObject(response);
                JSONArray liste = json.getJSONArray("list");

                List<Prevision> previsions = new ArrayList<>();
                for (int i = 8; i < liste.length() && previsions.size() < 6; i += 8) {
                    JSONObject item = liste.getJSONObject(i);
                    JSONObject main = item.getJSONObject("main");
                    JSONObject weather = item.getJSONArray("weather").getJSONObject(0);

                    long timestamp = item.getLong("dt") * 1000L;
                    String jour = new SimpleDateFormat("EEE", Locale.FRENCH)
                            .format(new Date(timestamp));
                    jour = jour.substring(0, 1).toUpperCase() + jour.substring(1);

                    Prevision p = new Prevision();
                    p.jour = jour;
                    p.temperature = main.getDouble("temp");
                    p.iconeCode = weather.getString("icon");
                    previsions.add(p);
                }
                callback.onPrevisions(previsions);

            } catch (Exception e) {
                callback.onPrevisions(new ArrayList<>());
            }
        }).start();
    }

    public void getPrevisions(String ville, PrevisionCallback callback) {
        new Thread(() -> {
            try {
                String urlString = URL_PREVISIONS + "?q=" + ville
                        + "&appid=" + API_KEY
                        + "&units=metric&lang=fr&cnt=48";

                String response = fetch(urlString);
                JSONObject json = new JSONObject(response);
                JSONArray liste = json.getJSONArray("list");

                List<Prevision> previsions = new ArrayList<>();
                // Prendre une prévision par jour (toutes les 8 entrées = 1 jour)
                for (int i = 8; i < liste.length() && previsions.size() < 6; i += 8) {
                    JSONObject item = liste.getJSONObject(i);
                    JSONObject main = item.getJSONObject("main");
                    JSONObject weather = item.getJSONArray("weather").getJSONObject(0);

                    long timestamp = item.getLong("dt") * 1000L;
                    String jour = new SimpleDateFormat("EEE", Locale.FRENCH)
                            .format(new Date(timestamp));
                    jour = jour.substring(0, 1).toUpperCase() + jour.substring(1);

                    Prevision p = new Prevision();
                    p.jour = jour;
                    p.temperature = main.getDouble("temp");
                    p.iconeCode = weather.getString("icon");
                    previsions.add(p);
                }

                callback.onPrevisions(previsions);

            } catch (Exception e) {
                callback.onPrevisions(new ArrayList<>());
            }
        }).start();
    }

    private String fetch(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }
}