package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;

public class PaiementDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_paiement, container, false);

        EditText editCarte  = view.findViewById(R.id.edit_carte);
        EditText editExpiry = view.findViewById(R.id.edit_expiry);
        EditText editCvc    = view.findViewById(R.id.edit_cvc);
        EditText editNom    = view.findViewById(R.id.edit_nom_carte);
        Button btnPayer     = view.findViewById(R.id.btn_payer);
        TextView btnRetour  = view.findViewById(R.id.btn_retour_dialog);
        ProgressBar progress = view.findViewById(R.id.progress_paiement);
        TextView txtResultat = view.findViewById(R.id.txt_resultat);

        btnRetour.setOnClickListener(v -> dismiss());

        btnPayer.setOnClickListener(v -> {
            String carte = editCarte.getText().toString().trim();
            String expiry = editExpiry.getText().toString().trim();
            String cvc = editCvc.getText().toString().trim();
            String nom = editNom.getText().toString().trim();

            if (carte.isEmpty() || expiry.isEmpty()
                    || cvc.isEmpty() || nom.isEmpty()) {
                txtResultat.setText("⚠️ Veuillez remplir tous les champs.");
                txtResultat.setTextColor(
                        requireContext().getColor(R.color.alerte_rouge));
                txtResultat.setVisibility(View.VISIBLE);
                return;
            }

            // Simulation paiement
            progress.setVisibility(View.VISIBLE);
            btnPayer.setEnabled(false);

            new android.os.Handler().postDelayed(() -> {
                progress.setVisibility(View.GONE);

                // Activer Claude dans SharedPreferences
                requireActivity()
                        .getSharedPreferences(AuthActivity.PREFS, 0)
                        .edit()
                        .putBoolean("claude_actif", true)
                        .apply();

                txtResultat.setText("✅ Paiement réussi ! Claude AI activé.");
                txtResultat.setTextColor(
                        requireContext().getColor(R.color.vert_principal));
                txtResultat.setVisibility(View.VISIBLE);
                btnPayer.setText("Activé ✅");

            }, 2000);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
