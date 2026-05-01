package com.example.tryproject.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.tryproject.MainActivity;
import com.example.tryproject.R;
import com.example.tryproject.data.MeteoRepository;
import com.example.tryproject.model.Meteo;

public class MeteoNotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "agrico_meteo";
    public static final int NOTIF_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        String ville = context.getSharedPreferences("agrico_prefs", 0)
                .getString("ville", "Casablanca"); // ✅ clé corrigée

        new Thread(() -> {
            MeteoRepository repo = new MeteoRepository();
            repo.getMeteo(ville, new MeteoRepository.MeteoCallback() {
                @Override
                public void onMeteo(Meteo meteo) {
                    String titre = "🌿 AgroSmart — Météo du jour";
                    String message = meteo.ville + " : " +
                            Math.round(meteo.temperature) + "°C, " +
                            meteo.description + "\n" +
                            genererConseilCourt(meteo);
                    envoyerNotification(context, titre, message);
                }

                @Override
                public void onErreur(String erreur) {
                    envoyerNotification(context,
                            "🌿 AgroSmart — Bonjour !",
                            "Consultez la météo et planifiez votre journée agricole.");
                }
            });
        }).start(); // ✅ thread lancé
    }

    private String genererConseilCourt(Meteo meteo) {
        if (meteo.temperature > 35)
            return "💡 Arrosez tôt le matin !";
        if (meteo.description.contains("pluie"))
            return "💡 Pas besoin d'irriguer aujourd'hui.";
        if (meteo.humidite > 85)
            return "💡 Surveillez les champignons.";
        if (meteo.vitesseVent > 30)
            return "💡 Évitez les traitements aujourd'hui.";
        return "💡 Bonne journée pour travailler !";
    }

    public static void envoyerNotification(Context context,
                                           String titre, String message) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Créer le canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Météo Agricole",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Rappels météo quotidiens");
            manager.createNotificationChannel(channel);
        }

        // Intent pour ouvrir l'app au clic
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(titre)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        manager.notify(NOTIF_ID, builder.build());
    }
}