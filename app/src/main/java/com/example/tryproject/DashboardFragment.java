package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.tryproject.data.AgricoDatabase;
import com.example.tryproject.data.MeteoRepository;
import com.example.tryproject.model.Activite;
import com.example.tryproject.model.Meteo;
import com.example.tryproject.notifications.NotificationScheduler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView txtNom, txtDate, txtSalutation;
    private TextView dashIconeMeteo, dashVille, dashDesc, dashConseil, dashTemp;
    private TextView dashNbActivites;
    private TextView iconeNotif, txtNotifStatut;
    private LinearLayout layoutActivites, btnToggleNotifs;
    private boolean notifsActives;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtNom          = view.findViewById(R.id.txt_nom_dashboard);
        txtDate         = view.findViewById(R.id.txt_date_dashboard);
        txtSalutation   = view.findViewById(R.id.txt_salutation);
        dashIconeMeteo  = view.findViewById(R.id.dash_icone_meteo);
        dashVille       = view.findViewById(R.id.dash_ville_meteo);
        dashDesc        = view.findViewById(R.id.dash_desc_meteo);
        dashConseil     = view.findViewById(R.id.dash_conseil_meteo);
        dashTemp        = view.findViewById(R.id.dash_temp_meteo);
        dashNbActivites = view.findViewById(R.id.dash_nb_activites);
        layoutActivites = view.findViewById(R.id.layout_activites_dashboard);
        btnToggleNotifs = view.findViewById(R.id.btn_toggle_notifs);
        iconeNotif      = view.findViewById(R.id.icone_notif);
        txtNotifStatut  = view.findViewById(R.id.txt_notif_statut);

        // Charger état notifs
        notifsActives = requireActivity()
                .getSharedPreferences("agrico_prefs", 0)
                .getBoolean("notifs_actives", true);
        mettreAJourBoutonNotif();

        // Toggle notifs
        btnToggleNotifs.setOnClickListener(v -> toggleNotifications());

        // Nom et salutation
        String nom = requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0)
                .getString(AuthActivity.KEY_NOM, "Agriculteur");
        txtNom.setText(nom);

        int heure = new Date().getHours();
        if (heure < 12) txtSalutation.setText("Bonjour 🌅");
        else if (heure < 18) txtSalutation.setText("Bon après-midi ☀️");
        else txtSalutation.setText("Bonsoir 🌙");

        String date = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)
                .format(new Date());
        txtDate.setText(date.substring(0, 1).toUpperCase() + date.substring(1));

        // Météo
        String ville = requireActivity()
                .getSharedPreferences("agrico_prefs", 0)
                .getString("ville_meteo", "Casablanca");
        chargerMeteo(ville);

        // Activités
        chargerActivitesDuJour();

        // Raccourcis
        view.findViewById(R.id.raccourci_chatbot).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.chatbotFragment));
        view.findViewById(R.id.raccourci_base).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.baseFragment));
        view.findViewById(R.id.raccourci_calendrier).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.calendrierFragment));
        view.findViewById(R.id.carte_meteo_dashboard).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.meteoFragment));

        return view;
    }

    private void toggleNotifications() {
        notifsActives = !notifsActives;

        // Sauvegarder l'état
        requireActivity()
                .getSharedPreferences("agrico_prefs", 0)
                .edit()
                .putBoolean("notifs_actives", notifsActives)
                .apply();

        if (notifsActives) {
            NotificationScheduler.programmerNotificationQuotidienne(requireContext());
        } else {
            NotificationScheduler.annulerNotifications(requireContext());
        }

        mettreAJourBoutonNotif();
    }

    private void mettreAJourBoutonNotif() {
        if (notifsActives) {
            iconeNotif.setText("🔔");
            txtNotifStatut.setText("ON");
            btnToggleNotifs.setBackgroundResource(R.drawable.cercle_notif_actif);
        } else {
            iconeNotif.setText("🔕");
            txtNotifStatut.setText("OFF");
            btnToggleNotifs.setBackgroundResource(R.drawable.cercle_notif_inactif);
        }
    }

    private void chargerMeteo(String ville) {
        new MeteoRepository().getMeteo(ville, new MeteoRepository.MeteoCallback() {
            @Override
            public void onMeteo(Meteo meteo) {
                requireActivity().runOnUiThread(() -> {
                    dashIconeMeteo.setText(iconeEmoji(meteo.iconeCode));
                    dashVille.setText(meteo.ville);
                    dashDesc.setText(capitaliser(meteo.description));
                    dashTemp.setText(Math.round(meteo.temperature) + "°");
                    dashConseil.setText(conseilCourt(meteo));
                });
            }
            @Override
            public void onErreur(String e) {}
        });
    }

    private void chargerActivitesDuJour() {
        new Thread(() -> {
            List<Activite> toutes = AgricoDatabase
                    .getInstance(getContext())
                    .activiteDao().getTout();

            String aujourdhui = new SimpleDateFormat("dd/MM", Locale.FRENCH)
                    .format(new Date());

            List<Activite> duJour = new ArrayList<>();
            for (Activite a : toutes) {
                if (a.date != null && a.date.startsWith(aujourdhui)) {
                    duJour.add(a);
                }
            }

            requireActivity().runOnUiThread(() -> {
                layoutActivites.removeAllViews();
                if (duJour.isEmpty()) {
                    TextView vide = new TextView(getContext());
                    vide.setText("Aucune activité prévue aujourd'hui 🌿");
                    vide.setTextColor(0xFF757575);
                    vide.setPadding(0, 8, 0, 16);
                    layoutActivites.addView(vide);
                    dashNbActivites.setText("0 activité");
                } else {
                    dashNbActivites.setText(duJour.size() + " activité(s)");
                    for (Activite a : duJour) {
                        View item = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_activite,
                                        layoutActivites, false);
                        ((TextView) item.findViewById(R.id.item_titre))
                                .setText(a.type + " — " + a.culture);
                        ((TextView) item.findViewById(R.id.item_date))
                                .setText("📅 " + a.date);
                        ((TextView) item.findViewById(R.id.item_note))
                                .setText(a.note != null ? a.note : "");
                        layoutActivites.addView(item);
                    }
                }
            });
        }).start();
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

    private String conseilCourt(Meteo meteo) {
        if (meteo.temperature > 35) return "💡 Arrosez tôt le matin !";
        if (meteo.description.contains("pluie")) return "💡 Pas besoin d'irriguer.";
        if (meteo.humidite > 85) return "💡 Risque fongique élevé.";
        if (meteo.vitesseVent > 30) return "💡 Évitez les traitements.";
        return "💡 Bonne journée pour travailler !";
    }

    private String capitaliser(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
