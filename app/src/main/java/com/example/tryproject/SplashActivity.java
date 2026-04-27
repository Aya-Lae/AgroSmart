package com.example.tryproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Après 1.5 secondes, décider où aller
        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(
                    AuthActivity.PREFS, MODE_PRIVATE);
            boolean estConnecte = prefs.getBoolean(AuthActivity.KEY_CONNECTE, false);

            if (estConnecte) {
                // Déjà inscrit → aller directement à l'app
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // Première fois → aller à l'inscription
                startActivity(new Intent(this, AuthActivity.class));
            }
            finish();
        }, 1500);
    }
}