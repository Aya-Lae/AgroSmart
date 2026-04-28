package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tryproject.data.MeteoRepository;
import com.example.tryproject.model.Meteo;
import com.example.tryproject.model.Prevision;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeteoFragment extends Fragment {

    private TextView txtVille, txtDate, txtTemperature, txtMinMax;
    private TextView txtDescription, txtIcone, txtHumidite, txtVent;
    private TextView txtSunset, txtConseil, txtErreur, txtAlerteBadge;
    private LinearLayout layoutPrevisions;
    private EditText editVille;
    private MeteoRepository repository;

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

        // Date du jour
        String dateAujourdhui = new SimpleDateFormat("EEE d MMM", Locale.FRENCH)
                .format(new Date());
        txtDate.setText(dateAujourdhui.substring(0, 1).toUpperCase()
                + dateAujourdhui.substring(1));

        // Charger ville par défaut
        chargerMeteo("Casablanca");

        btnChercher.setOnClickListener(v -> {
            String ville = editVille.getText().toString().trim();
            if (!ville.isEmpty()) chargerMeteo(ville);
        });

        return view;
    }

    private void chargerMeteo(String ville) {
        txtErreur.setVisibility(View.GONE);

        repository.getMeteo(ville, new MeteoRepository.MeteoCallback() {
            @Override
            public void onMeteo(Meteo meteo) {
                requireActivity().runOnUiThread(() -> afficherMeteo(meteo));
                // Charger aussi les prévisions
                repository.getPrevisions(ville, previsions ->
                        requireActivity().runOnUiThread(() ->
                                afficherPrevisions(previsions)));
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
        txtVille.setText(meteo.ville);
        txtTemperature.setText(Math.round(meteo.temperature) + "°C");
        txtMinMax.setText(Math.round(meteo.temperatureMax) + "°C / "
                + Math.round(meteo.temperatureMin) + "°C");
        txtDescription.setText(capitaliser(meteo.description));
        txtHumidite.setText(meteo.humidite + "%");
        txtVent.setText(Math.round(meteo.vitesseVent) + " km/h");
        txtSunset.setText(meteo.sunset);
        txtIcone.setText(iconeEmoji(meteo.iconeCode));

        // Alerte
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

    // Convertit le code OpenWeather en emoji
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
        if (code.startsWith("50")) return "🌫";
        return "🌤";
    }

    private String genererConseil(Meteo meteo) {
        if (meteo.humidite > 85)
            return "💧 Humidité très élevée. Évitez d'irriguer et surveillez l'apparition de champignons sur vos cultures.";
        if (meteo.temperature > 35)
            return "🌡️ Forte chaleur. Privilégiez l'arrosage tôt le matin avant 8h ou le soir après 19h pour limiter l'évaporation.";
        if (meteo.vitesseVent > 30)
            return "💨 Vent fort aujourd'hui. Évitez les traitements phytosanitaires — ils seraient emportés par le vent.";
        if (meteo.description.contains("pluie") || meteo.description.contains("pluvieux"))
            return "🌧 Pluie prévue. Pas besoin d'irriguer aujourd'hui. Bonne journée pour les travaux en intérieur.";
        if (meteo.temperature < 5)
            return "🥶 Températures basses — risque de gel. Protégez les jeunes plants et les cultures sensibles.";
        return "✅ Conditions favorables aujourd'hui. Bonne journée pour les semis, la taille ou le traitement de vos cultures.";
    }

    private String capitaliser(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}