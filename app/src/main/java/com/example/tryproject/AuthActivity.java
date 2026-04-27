package com.example.tryproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    public static final String PREFS      = "agrico_prefs";
    public static final String KEY_CONNECTE = "est_connecte";
    public static final String KEY_NOM    = "nom";
    public static final String KEY_REGION = "region";
    public static final String KEY_CULTURE = "culture";
    public static final String KEY_EMAIL  = "email";
    public static final String KEY_PASSWORD = "password";

    private boolean modeConnexion = true;
    private boolean mdpVisible    = false;

    private TextView tabConnexion, tabInscription, btnAction, txtErreur, btnVoirMdp;
    private EditText editEmail, editPassword, editNom, editRegion, editCulture;
    private LinearLayout champNom, champRegion, champCulture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabConnexion   = findViewById(R.id.tab_connexion);
        tabInscription = findViewById(R.id.tab_inscription);
        btnAction      = findViewById(R.id.btn_action);
        txtErreur      = findViewById(R.id.txt_erreur);
        btnVoirMdp     = findViewById(R.id.btn_voir_mdp);
        editEmail      = findViewById(R.id.edit_email);
        editPassword   = findViewById(R.id.edit_password);
        editNom        = findViewById(R.id.edit_nom);
        editRegion     = findViewById(R.id.edit_region);
        editCulture    = findViewById(R.id.edit_culture);
        champNom       = findViewById(R.id.champ_nom);
        champRegion    = findViewById(R.id.champ_region);
        champCulture   = findViewById(R.id.champ_culture);

        // Switch tabs
        tabConnexion.setOnClickListener(v -> switchMode(true));
        tabInscription.setOnClickListener(v -> switchMode(false));

        // Voir/cacher mot de passe
        btnVoirMdp.setOnClickListener(v -> {
            mdpVisible = !mdpVisible;
            editPassword.setInputType(mdpVisible
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editPassword.setSelection(editPassword.length());
            btnVoirMdp.setText(mdpVisible ? "🙈" : "👁");
        });

        btnAction.setOnClickListener(v -> {
            if (modeConnexion) seConnecter();
            else sInscrire();
        });
    }

    private void switchMode(boolean connexion) {
        modeConnexion = connexion;
        txtErreur.setVisibility(View.GONE);

        if (connexion) {
            tabConnexion.setBackgroundResource(R.drawable.btn_green);
            tabConnexion.setTextColor(getColor(R.color.blanc));
            tabInscription.setBackgroundResource(R.drawable.btn_outline);
            tabInscription.setTextColor(getColor(R.color.vert_principal));
            champNom.setVisibility(View.GONE);
            champRegion.setVisibility(View.GONE);
            champCulture.setVisibility(View.GONE);
            btnAction.setText("Se connecter");
        } else {
            tabInscription.setBackgroundResource(R.drawable.btn_green);
            tabInscription.setTextColor(getColor(R.color.blanc));
            tabConnexion.setBackgroundResource(R.drawable.btn_outline);
            tabConnexion.setTextColor(getColor(R.color.vert_principal));
            champNom.setVisibility(View.VISIBLE);
            champRegion.setVisibility(View.VISIBLE);
            champCulture.setVisibility(View.VISIBLE);
            btnAction.setText("Créer mon compte");
        }
    }

    private void seConnecter() {
        String email    = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showErreur("Veuillez remplir tous les champs.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String emailSauvegarde  = prefs.getString(KEY_EMAIL, "");
        String mdpSauvegarde    = prefs.getString(KEY_PASSWORD, "");

        if (!email.equals(emailSauvegarde)) {
            showErreur("Email introuvable. Créez un compte d'abord.");
            return;
        }
        if (!password.equals(mdpSauvegarde)) {
            showErreur("Mot de passe incorrect.");
            return;
        }

        prefs.edit().putBoolean(KEY_CONNECTE, true).apply();
        allerVersApp();
    }

    private void sInscrire() {
        String nom      = editNom.getText().toString().trim();
        String region   = editRegion.getText().toString().trim();
        String culture  = editCulture.getText().toString().trim();
        String email    = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (nom.isEmpty() || region.isEmpty() || culture.isEmpty()
                || email.isEmpty() || password.isEmpty()) {
            showErreur("Veuillez remplir tous les champs.");
            return;
        }

        if (!email.contains("@")) {
            showErreur("Email invalide.");
            return;
        }

        if (password.length() < 6) {
            showErreur("Le mot de passe doit avoir au moins 6 caractères.");
            return;
        }

        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putBoolean(KEY_CONNECTE, true)
                .putString(KEY_NOM, nom)
                .putString(KEY_REGION, region)
                .putString(KEY_CULTURE, culture)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();

        allerVersApp();
    }

    private void showErreur(String msg) {
        txtErreur.setText(msg);
        txtErreur.setVisibility(View.VISIBLE);
    }

    private void allerVersApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}