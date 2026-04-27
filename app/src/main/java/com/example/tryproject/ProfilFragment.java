package com.example.tryproject;

import android.content.Intent;
import android.content.SharedPreferences;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        editNom          = view.findViewById(R.id.edit_nom);
        editRegion       = view.findViewById(R.id.edit_region);
        editCulture      = view.findViewById(R.id.edit_culture);
        txtConfirmation  = view.findViewById(R.id.txt_confirmation);
        Button btnSauvegarder   = view.findViewById(R.id.btn_sauvegarder);
        Button btnDeconnexion   = view.findViewById(R.id.btn_deconnexion);

        // Charger les infos depuis SharedPreferences
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0);
        editNom.setText(prefs.getString(AuthActivity.KEY_NOM, ""));
        editRegion.setText(prefs.getString(AuthActivity.KEY_REGION, ""));
        editCulture.setText(prefs.getString(AuthActivity.KEY_CULTURE, ""));

        btnSauvegarder.setOnClickListener(v -> sauvegarderProfil());

        btnDeconnexion.setOnClickListener(v -> {
            // Effacer la session
            requireActivity()
                    .getSharedPreferences(AuthActivity.PREFS, 0)
                    .edit()
                    .clear()
                    .apply();

            // Retourner à l'écran d'inscription
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void sauvegarderProfil() {
        // Mettre à jour SharedPreferences
        requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0)
                .edit()
                .putString(AuthActivity.KEY_NOM, editNom.getText().toString())
                .putString(AuthActivity.KEY_REGION, editRegion.getText().toString())
                .putString(AuthActivity.KEY_CULTURE, editCulture.getText().toString())
                .apply();

        txtConfirmation.setText("✅ Profil mis à jour !");
    }
}