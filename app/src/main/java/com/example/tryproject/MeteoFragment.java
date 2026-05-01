package com.example.tryproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.tryproject.data.MeteoRepository;
import com.example.tryproject.model.Meteo;
import com.example.tryproject.model.Prevision;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.tryproject.utils.ConseilsEngine;

public class MeteoFragment extends Fragment {

    private static final int REQUEST_LOCATION = 300;

    private TextView txtVille, txtDate, txtTemperature, txtMinMax;
    private TextView txtDescription, txtIcone, txtHumidite, txtVent;
    private TextView txtSunset, txtConseil, txtErreur, txtAlerteBadge;
    private LinearLayout layoutPrevisions;
    private EditText editVille;
    private MeteoRepository repository;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meteo, container, false);

        txtVille        = view.findViewById(R.id.txt_ville);
        txtDate         = view.findViewById(R.id.txt_date);
        txtTemperature  = view.findViewById(R.id.txt_temperature);
        txtMinMax       = view.findViewById(R.id.txt_minmax);
        txtDescription  = view.findViewById(R.id.txt_description);
        txtIcone        = view.findViewById(R.id.txt_icone);
        txtHumidite     = view.findViewById(R.id.txt_humidite);
        txtVent         = view.findViewById(R.id.txt_vent);
        txtSunset       = view.findViewById(R.id.txt_sunset);
        txtConseil      = view.findViewById(R.id.txt_conseil);
        txtErreur       = view.findViewById(R.id.txt_erreur);
        txtAlerteBadge  = view.findViewById(R.id.txt_alerte_badge);
        layoutPrevisions = view.findViewById(R.id.layout_previsions);
        editVille       = view.findViewById(R.id.edit_ville);
        Button btnChercher = view.findViewById(R.id.btn_chercher);

        repository = new MeteoRepository();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(
                requireActivity());

        // Date du jour
        String dateAujourdhui = new SimpleDateFormat("EEE d MMM", Locale.FRENCH)
                .format(new Date());
        txtDate.setText(dateAujourdhui.substring(0, 1).toUpperCase()
                + dateAujourdhui.substring(1));

        // Chercher par GPS automatiquement
        demanderLocalisation();

        // Bouton chercher manuellement
        btnChercher.setOnClickListener(v -> {
            String ville = editVille.getText().toString().trim();
            if (!ville.isEmpty()) {
                // Sauvegarder la ville choisie
                requireActivity()
                        .getSharedPreferences("agrico_prefs", 0)
                        .edit()
                        .putString("ville_meteo", ville)
                        .putBoolean("ville_manuelle", true)
                        .apply();
                chargerMeteoParVille(ville);
            }
        });

        return view;
    }

    private void demanderLocalisation() {
        // Si l'utilisateur a déjà choisi une ville manuellement
        boolean villeManuelle = requireActivity()
                .getSharedPreferences("agrico_prefs", 0)
                .getBoolean("ville_manuelle", false);

        if (villeManuelle) {
            String ville = requireActivity()
                    .getSharedPreferences("agrico_prefs", 0)
                    .getString("ville_meteo", "Casablanca");
            chargerMeteoParVille(ville);
            return;
        }

        // Sinon utiliser le GPS
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            utiliserGPS();
        }
    }

    private void utiliserGPS() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        txtVille.setText("Localisation...");

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        chargerMeteoParGPS(location.getLatitude(),
                                location.getLongitude());
                    } else {
                        // GPS indisponible → ville par défaut
                        chargerMeteoParVille("Casablanca");
                    }
                })
                .addOnFailureListener(e -> chargerMeteoParVille("Casablanca"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                utiliserGPS();
            } else {
                // Permission refusée → Casablanca par défaut
                chargerMeteoParVille("Casablanca");
            }
        }
    }

    private void chargerMeteoParGPS(double lat, double lon) {
        txtErreur.setVisibility(View.GONE);
        repository.getMeteoParCoordonnees(lat, lon,
                new MeteoRepository.MeteoCallback() {
                    @Override
                    public void onMeteo(Meteo meteo) {
                        // Sauvegarder la ville détectée
                        requireActivity()
                                .getSharedPreferences("agrico_prefs", 0)
                                .edit()
                                .putString("ville_meteo", meteo.ville)
                                .apply();

                        requireActivity().runOnUiThread(() -> afficherMeteo(meteo));

                        repository.getPrevisionParCoordonnees(lat, lon, previsions ->
                                requireActivity().runOnUiThread(() ->
                                        afficherPrevisions(previsions)));
                    }
                    @Override
                    public void onErreur(String erreur) {
                        requireActivity().runOnUiThread(() ->
                                chargerMeteoParVille("Casablanca"));
                    }
                });
    }

    private void chargerMeteoParVille(String ville) {
        txtErreur.setVisibility(View.GONE);
        repository.getMeteo(ville, new MeteoRepository.MeteoCallback() {
            @Override
            public void onMeteo(Meteo meteo) {
                requireActivity().runOnUiThread(() -> afficherMeteo(meteo));
                repository.getPrevisions(ville, previsions ->
                        requireActivity().runOnUiThread(() ->
                                afficherPrevisions(previsions)));
            }
            @Override
            public void onErreur(String erreur) {
                requireActivity().runOnUiThread(() -> {
                    txtErreur.setText("Ville introuvable. Essayez en anglais : ex: Tangier, Fez");
                    txtErreur.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void afficherMeteo(Meteo meteo) {
        txtVille.setText("📍 " + meteo.ville);
        txtTemperature.setText(Math.round(meteo.temperature) + "°C");
        txtMinMax.setText(Math.round(meteo.temperatureMax) + "°C / "
                + Math.round(meteo.temperatureMin) + "°C");
        txtDescription.setText(capitaliser(meteo.description));
        txtHumidite.setText(meteo.humidite + "%");
        txtVent.setText(Math.round(meteo.vitesseVent) + " km/h");
        txtSunset.setText(meteo.sunset);
        txtIcone.setText(iconeEmoji(meteo.iconeCode));

        if (meteo.alerte != null) {
            txtAlerteBadge.setVisibility(View.VISIBLE);
        }
        txtConseil.setText(genererConseil(meteo));
    }

    private void afficherPrevisions(List<Prevision> previsions) {
        layoutPrevisions.removeAllViews();
        for (Prevision p : previsions) {
            View item = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_prevision, layoutPrevisions, false);
            ((TextView) item.findViewById(R.id.prev_jour)).setText(p.jour);
            ((TextView) item.findViewById(R.id.prev_icone))
                    .setText(iconeEmoji(p.iconeCode));
            ((TextView) item.findViewById(R.id.prev_temp))
                    .setText(Math.round(p.temperature) + "°C");
            layoutPrevisions.addView(item);
        }
    }

    private String iconeEmoji(String code) {
        if (code == null) return "🌤";
        if (code.startsWith("01")) return "☀️";
        if (code.startsWith("02")) return "🌤";
        if (code.startsWith("03")) return "⛅";
        if (code.startsWith("04")) return "☁️";
        if (code.startsWith("09")) return "🌧";
        if (code.startsWith("10")) return "🌦";
        if (code.startsWith("11")) return "⛈";
        if (code.startsWith("13")) return "❄️";
        return "🌤";
    }

    private String genererConseil(Meteo meteo) {
        String culture = requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0)
                .getString(AuthActivity.KEY_CULTURE, "");
        String region = requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0)
                .getString(AuthActivity.KEY_REGION, "");

        return ConseilsEngine.genererConseil(meteo, culture, region);
    }

    private String capitaliser(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}