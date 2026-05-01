package com.example.tryproject.data;

import com.example.tryproject.model.Message;
import java.util.List;

public class ChatbotRepository {

    public interface ReponseCallback {
        void onReponse(String reponse);
        void onErreur(String erreur);
    }

    public void envoyerMessage(List<Message> historique, ReponseCallback callback) {
        String dernierMessage = historique.get(historique.size() - 1)
                .texte.toLowerCase();

        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            String reponse;

            if (dernierMessage.contains("maladie") || dernierMessage.contains("symptome")
                    || dernierMessage.contains("symptôme") || dernierMessage.contains("malade")) {
                reponse = "🔬 Pour diagnostiquer une maladie, décrivez :\n" +
                        "• La couleur et l'aspect des feuilles atteintes\n" +
                        "• La localisation (feuilles, tiges, racines)\n" +
                        "• Depuis quand les symptômes sont apparus\n\n" +
                        "Vous pouvez aussi utiliser le bouton 📷 pour m'envoyer une photo !";

            } else if (dernierMessage.contains("blé") || dernierMessage.contains("ble")
                    || dernierMessage.contains("céréale")) {
                reponse = "🌾 Conseils pour le blé :\n\n" +
                        "• Semis : octobre-novembre\n" +
                        "• Besoin en eau : 400-500mm/saison\n" +
                        "• Fertilisation azotée en 2 apports\n" +
                        "• Maladies à surveiller : rouille jaune, mildiou\n" +
                        "• Récolte : juin-juillet à 14% d'humidité\n\n" +
                        "Consultez la fiche complète dans la Base de connaissances 📚";

            } else if (dernierMessage.contains("tomate")) {
                reponse = "🍅 Conseils pour la tomate :\n\n" +
                        "• Plantation : mars-avril\n" +
                        "• Arrosage régulier au goutte-à-goutte\n" +
                        "• Ne pas mouiller les feuilles\n" +
                        "• Pincez les gourmands régulièrement\n" +
                        "• Surveillez : mildiou, mouche blanche\n\n" +
                        "Par forte chaleur, arrosez tôt le matin !";

            } else if (dernierMessage.contains("olive") || dernierMessage.contains("olivier")) {
                reponse = "🫒 Conseils pour l'olivier :\n\n" +
                        "• Très résistant à la sécheresse\n" +
                        "• Taille : février-mars\n" +
                        "• Irrigation d'appoint en été (+40% rendement)\n" +
                        "• Récolte : octobre-décembre\n" +
                        "• Surveillez la mouche de l'olive en automne";

            } else if (dernierMessage.contains("irrigation") || dernierMessage.contains("arrosage")
                    || dernierMessage.contains("eau")) {
                reponse = "💧 Conseils d'irrigation :\n\n" +
                        "• Arrosez tôt le matin (avant 8h) ou le soir (après 19h)\n" +
                        "• Le goutte-à-goutte économise 40% d'eau\n" +
                        "• Évitez d'arroser les jours de vent fort\n" +
                        "• Paillez le sol pour réduire l'évaporation\n" +
                        "• Consultez la météo avant d'irriguer";

            } else if (dernierMessage.contains("engrais") || dernierMessage.contains("fertilisation")
                    || dernierMessage.contains("fertilisant") || dernierMessage.contains("sol")) {
                reponse = "🌱 Conseils de fertilisation :\n\n" +
                        "• Analysez votre sol avant tout apport\n" +
                        "• Azote (N) → croissance des feuilles\n" +
                        "• Phosphore (P) → développement des racines\n" +
                        "• Potassium (K) → résistance aux maladies\n" +
                        "• Fumier organique : appliquez en automne";

            } else if (dernierMessage.contains("météo") || dernierMessage.contains("meteo")
                    || dernierMessage.contains("pluie") || dernierMessage.contains("chaleur")
                    || dernierMessage.contains("température")) {
                reponse = "🌤️ Pour la météo de votre région :\n\n" +
                        "Consultez l'onglet Météo de l'application pour :\n" +
                        "• La température et conditions actuelles\n" +
                        "• Les prévisions sur 6 jours\n" +
                        "• Les conseils agricoles du jour\n" +
                        "• Les alertes (canicule, gel, vent fort)";

            } else if (dernierMessage.contains("bonjour") || dernierMessage.contains("salut")
                    || dernierMessage.contains("salam") || dernierMessage.contains("hello")) {
                reponse = "👋 Bonjour ! Je suis AgroSmart, votre assistant agricole.\n\n" +
                        "Je peux vous aider avec :\n" +
                        "• 🔬 Diagnostic de maladies (envoyez une photo !)\n" +
                        "• 💧 Conseils d'irrigation\n" +
                        "• 🌱 Fertilisation et sol\n" +
                        "• 📅 Calendrier des travaux\n" +
                        "• 🌾 Toutes vos cultures\n\n" +
                        "Quelle est votre question ?";

            } else if (dernierMessage.contains("merci") || dernierMessage.contains("شكرا")) {
                reponse = "😊 Avec plaisir ! N'hésitez pas si vous avez d'autres questions.\n" +
                        "Bonne journée et bonne récolte ! 🌾";

            } else if (dernierMessage.contains("gel") || dernierMessage.contains("froid")
                    || dernierMessage.contains("hiver")) {
                reponse = "❄️ Protection contre le gel :\n\n" +
                        "• Utilisez des voiles d'hivernage la nuit\n" +
                        "• Paillez le sol autour des racines\n" +
                        "• Arrosez le soir (l'eau protège du gel léger)\n" +
                        "• Protégez particulièrement : agrumes, tomates, poivrons\n" +
                        "• L'olivier résiste jusqu'à -10°C une fois adulte";

            } else if (dernierMessage.contains("récolte") || dernierMessage.contains("recolte")) {
                reponse = "🌾 Conseils de récolte :\n\n" +
                        "• Récoltez tôt le matin par temps frais\n" +
                        "• Évitez de récolter après une pluie\n" +
                        "• Stockez dans un endroit frais et aéré\n\n" +
                        "Quelle culture souhaitez-vous récolter ? " +
                        "Je peux vous donner des conseils spécifiques !";

            } else {
                reponse = "🤔 Bonne question ! Pour vous donner le meilleur conseil, " +
                        "pouvez-vous préciser :\n\n" +
                        "• Votre type de culture\n" +
                        "• Les symptômes ou problèmes observés\n" +
                        "• Votre région\n\n" +
                        "Ou envoyez une photo 📷 pour un diagnostic visuel !";
            }

            callback.onReponse(reponse);
        }).start();
    }
}