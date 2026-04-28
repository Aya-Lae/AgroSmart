package com.example.tryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private boolean estArabe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restaurer la langue AVANT setContentView
        SharedPreferences prefs = getSharedPreferences("agrico_prefs", MODE_PRIVATE);
        String langue = prefs.getString("langue", "fr");
        estArabe = langue.equals("ar");
        appliquerLangue(langue);

        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Bouton langue — APRÈS setContentView
        TextView btnLangue = findViewById(R.id.btn_langue);
        btnLangue.setText(estArabe ? "AR 🌐" : "FR 🌐");
        btnLangue.setOnClickListener(v -> {
            estArabe = !estArabe;
            String nouvelleLangue = estArabe ? "ar" : "fr";
            getSharedPreferences("agrico_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("langue", nouvelleLangue)
                    .apply();
            changerLangue(nouvelleLangue);
        });
    }

    private void appliquerLangue(String langue) {
        Locale locale = new Locale(langue);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void changerLangue(String langue) {
        appliquerLangue(langue);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}