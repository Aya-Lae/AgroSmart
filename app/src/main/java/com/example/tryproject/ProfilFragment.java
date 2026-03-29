package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tryproject.data.AgricoDatabase;
import com.example.tryproject.model.Agriculteur;

public class ProfilFragment extends Fragment {

    EditText editNom, editRegion, editCulture;
    TextView txtConfirmation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        editNom = view.findViewById(R.id.edit_nom);
        editRegion = view.findViewById(R.id.edit_region);
        editCulture = view.findViewById(R.id.edit_culture);
        txtConfirmation = view.findViewById(R.id.txt_confirmation);
        Button btnSauvegarder = view.findViewById(R.id.btn_sauvegarder);

        // Charger le profil existant au démarrage
        chargerProfil();

        // Quand on clique sur Sauvegarder
        btnSauvegarder.setOnClickListener(v -> sauvegarderProfil());

        return view;
    }

    private void sauvegarderProfil() {
        // Room interdit les opérations réseau/base sur le thread principal
        // donc on utilise un thread séparé
        new Thread(() -> {
            Agriculteur agriculteur = new Agriculteur();
            agriculteur.nom = editNom.getText().toString();
            agriculteur.region = editRegion.getText().toString();
            agriculteur.culture = editCulture.getText().toString();
            agriculteur.langue = "fr";

            AgricoDatabase.getInstance(getContext())
                    .agriculteurDao()
                    .sauvegarder(agriculteur);

            // Revenir sur le thread principal pour mettre à jour l'écran
            requireActivity().runOnUiThread(() ->
                    txtConfirmation.setText("Profil sauvegardé !")
            );
        }).start();
    }

    private void chargerProfil() {
        new Thread(() -> {
            Agriculteur agriculteur = AgricoDatabase.getInstance(getContext())
                    .agriculteurDao()
                    .charger();

            if (agriculteur != null) {
                requireActivity().runOnUiThread(() -> {
                    editNom.setText(agriculteur.nom);
                    editRegion.setText(agriculteur.region);
                    editCulture.setText(agriculteur.culture);
                });
            }
        }).start();
    }
}