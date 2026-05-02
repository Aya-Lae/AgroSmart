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
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.tryproject.notifications.NotificationScheduler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;

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
        // Demander permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        200);
            }
        }

// Programmer la notification météo quotidienne
        NotificationScheduler.programmerNotificationQuotidienne(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Référence à la barre du haut
        LinearLayout barreHaut = findViewById(R.id.barre_haut);

// Cacher/afficher selon la page
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.meteoFragment) {
                barreHaut.setVisibility(View.GONE);
            } else {
                barreHaut.setVisibility(View.VISIBLE);
            }
        });

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

        // Bouton notification dans la barre principale
        ImageView iconeNotifMain = findViewById(R.id.icone_notif_main);
        TextView txtNotifMain = findViewById(R.id.txt_notif_main);
        LinearLayout btnNotifsMain = findViewById(R.id.btn_toggle_notifs_main);

        boolean[] notifsActives = {getSharedPreferences("agrico_prefs", 0)
                .getBoolean("notifs_actives", true)};

        mettreAJourNotifMain(iconeNotifMain, txtNotifMain, btnNotifsMain, notifsActives[0]);

        btnNotifsMain.setOnClickListener(v -> {
            notifsActives[0] = !notifsActives[0];
            getSharedPreferences("agrico_prefs", 0)
                    .edit()
                    .putBoolean("notifs_actives", notifsActives[0])
                    .apply();
            if (notifsActives[0]) {
                NotificationScheduler.programmerNotificationQuotidienne(this);
            } else {
                NotificationScheduler.annulerNotifications(this);
            }
            mettreAJourNotifMain(iconeNotifMain, txtNotifMain, btnNotifsMain, notifsActives[0]);
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

    private void mettreAJourNotifMain(ImageView icone, TextView txt,
                                      LinearLayout btn, boolean actif) {
        icone.setImageResource(R.drawable.ic_notification);
        txt.setText(actif ? "ON" : "OFF");
        btn.setBackgroundResource(actif ?
                R.drawable.cercle_notif_actif :
                R.drawable.cercle_notif_inactif);
    }
}