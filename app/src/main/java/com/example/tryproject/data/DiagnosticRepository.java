package com.example.tryproject.data;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DiagnosticRepository {

    public interface DiagnosticCallback {
        void onDiagnostic(String resultat);
    }

    public void analyserPhoto(Bitmap photo, DiagnosticCallback callback) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simule le temps d'analyse
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Analyse basée sur les couleurs dominantes de la photo
            String diagnostic = analyserCouleurs(photo);
            callback.onDiagnostic(diagnostic);
        }).start();
    }

    private String analyserCouleurs(Bitmap bitmap) {
        // Réduire la taille pour l'analyse
        Bitmap petit = Bitmap.createScaledBitmap(bitmap, 50, 50, false);

        long rouge = 0, vert = 0, jaune = 0, marron = 0, total = 0;

        for (int x = 0; x < petit.getWidth(); x++) {
            for (int y = 0; y < petit.getHeight(); y++) {
                int pixel = petit.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                total++;

                // Détection couleurs
                if (r > 150 && g < 100 && b < 100) rouge++;        // Rouge = maladie
                else if (g > 100 && r < 100 && b < 100) vert++;    // Vert = sain
                else if (r > 150 && g > 150 && b < 80) jaune++;    // Jaune = carence
                else if (r > 100 && g < 80 && b < 60) marron++;    // Marron = pourriture
            }
        }

        double pctRouge  = (rouge  * 100.0) / total;
        double pctVert   = (vert   * 100.0) / total;
        double pctJaune  = (jaune  * 100.0) / total;
        double pctMarron = (marron * 100.0) / total;

        // Diagnostic selon couleur dominante
        if (pctVert > 40) {
            return "✅ Plante en bonne santé\n\n" +
                    "Votre plante semble saine. La couleur verte dominante indique " +
                    "une bonne chlorophylle.\n\n" +
                    "💡 Conseils :\n" +
                    "• Continuez l'arrosage régulier\n" +
                    "• Vérifiez l'exposition au soleil\n" +
                    "• Fertilisation mensuelle recommandée";

        } else if (pctJaune > 20) {
            return "⚠️ Carence nutritionnelle détectée\n\n" +
                    "Les feuilles jaunes indiquent une possible carence en azote " +
                    "ou en fer (chlorose).\n\n" +
                    "🔬 Diagnostic : Chlorose ferrique probable\n\n" +
                    "💊 Traitement recommandé :\n" +
                    "• Apport d'engrais riche en azote (N)\n" +
                    "• Traitement au sulfate de fer\n" +
                    "• Vérifier le pH du sol (idéal : 6.0-7.0)\n" +
                    "• Arrosage modéré";

        } else if (pctRouge > 15) {
            return "🔴 Maladie fongique suspectée\n\n" +
                    "Des taches rougeâtres ont été détectées. Cela peut indiquer " +
                    "une rouille ou une alternariose.\n\n" +
                    "🔬 Diagnostic probable : Rouille ou Alternaria\n\n" +
                    "💊 Traitement recommandé :\n" +
                    "• Retirer les feuilles atteintes immédiatement\n" +
                    "• Traitement fongicide à base de cuivre\n" +
                    "• Éviter de mouiller les feuilles\n" +
                    "• Améliorer la ventilation\n" +
                    "• Répéter le traitement après 10 jours";

        } else if (pctMarron > 15) {
            return "🟤 Pourriture ou brûlure détectée\n\n" +
                    "Les zones marron indiquent une possible pourriture racinaire " +
                    "ou une brûlure due à la chaleur.\n\n" +
                    "🔬 Diagnostic probable : Botrytis ou brûlure solaire\n\n" +
                    "💊 Traitement recommandé :\n" +
                    "• Réduire l'arrosage immédiatement\n" +
                    "• Traitement fongicide systémique\n" +
                    "• Protéger du soleil direct\n" +
                    "• Vérifier le drainage du sol\n" +
                    "• Supprimer les parties atteintes";

        } else {
            return "🔍 Analyse incomplète\n\n" +
                    "La photo n'est pas suffisamment claire pour un diagnostic précis.\n\n" +
                    "💡 Pour une meilleure analyse :\n" +
                    "• Prenez la photo en pleine lumière\n" +
                    "• Photographiez les feuilles atteintes de près\n" +
                    "• Évitez les reflets et les ombres\n" +
                    "• Décrivez aussi les symptômes par texte";
        }
    }
}