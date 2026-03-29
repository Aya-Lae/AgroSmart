package com.example.tryproject.data;

import com.example.tryproject.model.Message;
import java.util.List;

public class ChatbotRepository {

    public interface ReponseCallback {
        void onReponse(String reponse);
        void onErreur(String erreur);
    }

    public void envoyerMessage(List<Message> historique, ReponseCallback callback) {

        // On récupère le dernier message de l'utilisateur
        String dernierMessage = historique.get(historique.size() - 1).texte.toLowerCase();

        // Simuler un délai (comme si on attendait le réseau)
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1 seconde d'attente simulée
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Réponses simulées selon les mots-clés
            String reponse;

            if (dernierMessage.contains("maladie") || dernierMessage.contains("symptome") || dernierMessage.contains("symptôme")) {
                reponse = "Pour diagnostiquer une maladie, décrivez les symptômes : " +
                        "couleur des feuilles, taches, flétrissement... " +
                        "Vous pouvez aussi m'envoyer une photo de votre plante.";

            } else if (dernierMessage.contains("blé") || dernierMessage.contains("ble")) {
                reponse = "Le blé est très cultivé au Maroc. " +
                        "La période de semis idéale est octobre-novembre. " +
                        "Besoin en eau : 400-500mm par saison. " +
                        "Attention à la rouille et au mildiou en période humide.";

            } else if (dernierMessage.contains("olive") || dernierMessage.contains("olivier")) {
                reponse = "L'olivier est résistant à la sécheresse. " +
                        "Taille recommandée en février-mars. " +
                        "La récolte se fait entre octobre et décembre selon la variété.";

            } else if (dernierMessage.contains("tomate")) {
                reponse = "La tomate nécessite beaucoup d'eau et de soleil. " +
                        "Plantation : mars-avril. " +
                        "Arrosage régulier, évitez de mouiller les feuilles. " +
                        "Surveillez la mouche blanche et le mildiou.";

            } else if (dernierMessage.contains("irrigation") || dernierMessage.contains("eau") || dernierMessage.contains("arrosage")) {
                reponse = "Conseil d'irrigation : arrosez tôt le matin ou le soir " +
                        "pour limiter l'évaporation. " +
                        "Le goutte-à-goutte économise 40% d'eau par rapport à l'aspersion.";

            } else if (dernierMessage.contains("engrais") || dernierMessage.contains("fertilisation") || dernierMessage.contains("fertilisant")) {
                reponse = "Pour la fertilisation, analysez d'abord votre sol. " +
                        "En général : azote (N) pour la croissance, " +
                        "phosphore (P) pour les racines, " +
                        "potassium (K) pour la résistance aux maladies.";

            } else if (dernierMessage.contains("meteo") || dernierMessage.contains("météo") || dernierMessage.contains("pluie") || dernierMessage.contains("chaleur")) {
                reponse = "Consultez l'onglet Météo de l'application " +
                        "pour les prévisions sur 7 jours et les alertes " +
                        "adaptées à votre région.";

            } else if (dernierMessage.contains("bonjour") || dernierMessage.contains("salut") || dernierMessage.contains("salam")) {
                reponse = "Bonjour ! Je suis AgroSmart, votre assistant agricole. " +
                        "Posez-moi vos questions sur vos cultures, " +
                        "les maladies des plantes, l'irrigation ou la fertilisation.";

            } else {
                reponse = "Bonne question ! Pour vous donner le meilleur conseil, " +
                        "pouvez-vous préciser : votre région, " +
                        "le type de culture concernée, " +
                        "et les symptômes ou problèmes observés ?";
            }

            // Retourner la réponse
            callback.onReponse(reponse);

        }).start();
    }
}