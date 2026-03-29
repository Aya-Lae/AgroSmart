package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tryproject.data.MeteoRepository;
import com.example.tryproject.model.Meteo;

public class MeteoFragment extends Fragment {

    private TextView txtTemperature, txtVille, txtDescription;
    private TextView txtMinMax, txtHumidite, txtVent;
    private TextView txtAlerte, txtConseil, txtErreur;
    private EditText editVille;
    private MeteoRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meteo, container, false);

        txtTemperature = view.findViewById(R.id.txt_temperature);
        txtVille       = view.findViewById(R.id.txt_ville);
        txtDescription = view.findViewById(R.id.txt_description);
        txtMinMax      = view.findViewById(R.id.txt_minmax);
        txtHumidite    = view.findViewById(R.id.txt_humidite);
        txtVent        = view.findViewById(R.id.txt_vent);
        txtAlerte      = view.findViewById(R.id.txt_alerte);
        txtConseil     = view.findViewById(R.id.txt_conseil);
        txtErreur      = view.findViewById(R.id.txt_erreur);
        editVille      = view.findViewById(R.id.edit_ville);
        Button btnChercher = view.findViewById(R.id.btn_chercher);

        repository = new MeteoRepository();

        // Charger Settat par défaut au démarrage
        chargerMeteo("Settat");

        btnChercher.setOnClickListener(v -> {
            String ville = editVille.getText().toString().trim();
            if (!ville.isEmpty()) chargerMeteo(ville);
        });

        return view;
    }

    private void chargerMeteo(String ville) {
        txtErreur.setVisibility(View.GONE);
        txtAlerte.setVisibility(View.GONE);

        repository.getMeteo(ville, new MeteoRepository.MeteoCallback() {

            @Override
            public void onMeteo(Meteo meteo) {
                requireActivity().runOnUiThread(() -> afficherMeteo(meteo));
            }

            @Override
            public void onErreur(String erreur) {
                requireActivity().runOnUiThread(() -> {
                    txtErreur.setText(erreur);
                    txtErreur.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void afficherMeteo(Meteo meteo) {
        txtTemperature.setText(Math.round(meteo.temperature) + "°C");
        txtVille.setText(meteo.ville);
        txtDescription.setText(meteo.description);
        txtMinMax.setText("↓" + Math.round(meteo.temperatureMin)
                + "° ↑" + Math.round(meteo.temperatureMax) + "°");
        txtHumidite.setText("Humidité\n" + meteo.humidite + "%");
        txtVent.setText("Vent\n" + Math.round(meteo.vitesseVent) + " km/h");

        // Afficher l'alerte si nécessaire
        if (meteo.alerte != null) {
            txtAlerte.setText(meteo.alerte);
            txtAlerte.setVisibility(View.VISIBLE);
        }

        // Générer le conseil agricole selon la météo
        txtConseil.setText(genererConseil(meteo));
    }

    private String genererConseil(Meteo meteo) {
        if (meteo.humidite > 85) {
            return "Humidité très élevée aujourd'hui. Évitez d'irriguer et " +
                    "surveillez l'apparition de champignons sur vos cultures.";
        } else if (meteo.temperature > 35) {
            return "Forte chaleur prévue. Privilégiez l'arrosage tôt le matin " +
                    "avant 8h ou le soir après 19h pour limiter l'évaporation.";
        } else if (meteo.vitesseVent > 30) {
            return "Vent fort aujourd'hui. Évitez les traitements phytosanitaires " +
                    "— ils seraient emportés par le vent.";
        } else if (meteo.description.contains("pluie") || meteo.description.contains("pluvieux")) {
            return "Pluie prévue. Pas besoin d'irriguer aujourd'hui. " +
                    "Bonne journée pour les travaux en intérieur.";
        } else if (meteo.temperature < 5) {
            return "Températures basses — risque de gel. " +
                    "Protégez les jeunes plants et les cultures sensibles.";
        } else {
            return "Conditions favorables aujourd'hui. Bonne journée pour " +
                    "les semis, la taille ou le traitement de vos cultures.";
        }
    }
}