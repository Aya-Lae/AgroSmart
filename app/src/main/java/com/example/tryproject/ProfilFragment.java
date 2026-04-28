package com.example.tryproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilFragment extends Fragment {

    private EditText editNom, editRegion, editCulture;
    private TextView txtConfirmation, txtAvatar, txtNomHeader;
    private TextView txtEmailHeader, txtEmailDetail, txtStatutChatbot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        editNom          = view.findViewById(R.id.edit_nom);
        editRegion       = view.findViewById(R.id.edit_region);
        editCulture      = view.findViewById(R.id.edit_culture);
        txtConfirmation  = view.findViewById(R.id.txt_confirmation);
        txtAvatar        = view.findViewById(R.id.txt_avatar);
        txtNomHeader     = view.findViewById(R.id.txt_nom_header);
        txtEmailHeader   = view.findViewById(R.id.txt_email_header);
        txtEmailDetail   = view.findViewById(R.id.txt_email_detail);
        txtStatutChatbot = view.findViewById(R.id.txt_statut_chatbot);
        Button btnSauvegarder = view.findViewById(R.id.btn_sauvegarder);
        Button btnDeconnexion = view.findViewById(R.id.btn_deconnexion);
        LinearLayout carteActiver = view.findViewById(R.id.carte_activer);

        // Charger les données
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0);
        String nom     = prefs.getString(AuthActivity.KEY_NOM, "");
        String region  = prefs.getString(AuthActivity.KEY_REGION, "");
        String culture = prefs.getString(AuthActivity.KEY_CULTURE, "");
        boolean claudeActif = prefs.getBoolean("claude_actif", false);

        editNom.setText(nom);
        editRegion.setText(region);
        editCulture.setText(culture);

        // Firebase user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            txtEmailHeader.setText(email);
            txtEmailDetail.setText(email);
        }

        // Avatar : première lettre du nom
        if (!nom.isEmpty()) {
            txtAvatar.setText(String.valueOf(nom.charAt(0)).toUpperCase());
            txtNomHeader.setText(nom);
        }

        // Statut chatbot
        if (claudeActif) {
            txtStatutChatbot.setText("Claude AI ✅");
            txtStatutChatbot.setTextColor(
                    requireContext().getColor(R.color.vert_principal));
        } else {
            txtStatutChatbot.setText("Simulé");
        }

        // Bouton activer Claude
        carteActiver.setOnClickListener(v -> {
            // Ouvrir dialog paiement
            PaiementDialog dialog = new PaiementDialog();
            dialog.show(getParentFragmentManager(), "paiement");
        });

        btnSauvegarder.setOnClickListener(v -> sauvegarderProfil());

        btnDeconnexion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void sauvegarderProfil() {
        String nom = editNom.getText().toString().trim();
        requireActivity()
                .getSharedPreferences(AuthActivity.PREFS, 0)
                .edit()
                .putString(AuthActivity.KEY_NOM, nom)
                .putString(AuthActivity.KEY_REGION,
                        editRegion.getText().toString().trim())
                .putString(AuthActivity.KEY_CULTURE,
                        editCulture.getText().toString().trim())
                .apply();

        // Mettre à jour avatar
        if (!nom.isEmpty()) {
            txtAvatar.setText(String.valueOf(nom.charAt(0)).toUpperCase());
            txtNomHeader.setText(nom);
        }

        txtConfirmation.setText("✅ Profil mis à jour !");
    }
}