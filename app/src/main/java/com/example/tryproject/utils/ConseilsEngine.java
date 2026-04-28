package com.example.tryproject.utils;

import com.example.tryproject.model.Meteo;
import java.util.Calendar;

public class ConseilsEngine {

    // Génère un conseil personnalisé complet
    public static String genererConseil(Meteo meteo, String culture, String region) {
        StringBuilder conseil = new StringBuilder();

        // 1. Conseil météo immédiat
        conseil.append(conseilMeteo(meteo));
        conseil.append("\n\n");

        // 2. Conseil basé sur la culture
        if (culture != null && !culture.isEmpty()) {
            String conseilCulture = conseilParCulture(culture, meteo, getSaison());
            if (conseilCulture != null) {
                conseil.append("🌱 Pour votre culture (").append(culture).append(") :\n");
                conseil.append(conseilCulture);
                conseil.append("\n\n");
            }
        }

        // 3. Conseil saisonnier
        conseil.append(conseilSaisonnier(getSaison()));

        return conseil.toString().trim();
    }

    // Conseil basé sur la météo du moment
    private static String conseilMeteo(Meteo meteo) {
        if (meteo == null) return "💡 Consultez la météo avant de planifier vos travaux.";

        if (meteo.temperature > 38)
            return "🚨 Canicule ! Arrêtez tout travail extérieur entre 11h et 16h. " +
                    "Arrosez abondamment tôt le matin.";
        if (meteo.temperature > 32)
            return "🌡️ Forte chaleur (" + Math.round(meteo.temperature) + "°C). " +
                    "Privilégiez l'arrosage avant 8h ou après 19h pour limiter l'évaporation.";
        if (meteo.description != null && (meteo.description.contains("pluie")
                || meteo.description.contains("pluvieux")))
            return "🌧️ Pluie prévue. Pas besoin d'irriguer aujourd'hui. " +
                    "Profitez-en pour les travaux en intérieur ou la préparation du sol.";
        if (meteo.vitesseVent > 40)
            return "💨 Vent très fort (" + Math.round(meteo.vitesseVent) + " km/h). " +
                    "Évitez tout traitement phytosanitaire — il serait emporté par le vent.";
        if (meteo.vitesseVent > 25)
            return "💨 Vent modéré. Déconseillé pour les traitements. " +
                    "Bon moment pour inspecter vos cultures.";
        if (meteo.humidite > 85)
            return "💧 Humidité très élevée (" + meteo.humidite + "%). " +
                    "Risque élevé de maladies fongiques. Évitez d'irriguer et inspectez vos plantes.";
        if (meteo.temperature < 4)
            return "🥶 Risque de gel cette nuit ! Protégez vos cultures sensibles " +
                    "avec des voiles d'hivernage ou de la paille.";
        if (meteo.temperature < 10)
            return "❄️ Températures fraîches. Réduisez l'arrosage. " +
                    "Bonne période pour la taille des arbres fruitiers.";

        return "✅ Conditions favorables aujourd'hui. " +
                "Bonne journée pour les semis, la taille ou les traitements préventifs.";
    }

    // Conseil selon la culture et la météo
    private static String conseilParCulture(String culture, Meteo meteo, int saison) {
        String c = culture.toLowerCase().trim();

        if (c.contains("blé") || c.contains("ble") || c.contains("céréale")) {
            if (saison == 0) // Hiver
                return "Surveillez la rouille jaune par temps humide. " +
                        "Apport azoté recommandé au stade tallage.";
            if (saison == 1) // Printemps
                return "Période critique d'épiaison. Arrosage régulier indispensable. " +
                        "Traitez contre les pucerons si nécessaire.";
            if (saison == 2) // Été
                return "Période de maturation. Réduisez l'irrigation progressivement. " +
                        "Préparez la récolte quand le grain atteint 14% d'humidité.";
            return "Préparez le sol pour les semis d'automne (octobre-novembre).";
        }

        if (c.contains("tomate")) {
            if (meteo != null && meteo.temperature > 35)
                return "Attention à la nécrose apicale par forte chaleur ! " +
                        "Maintenez un arrosage régulier et constant. Paillez le sol.";
            if (meteo != null && meteo.humidite > 80)
                return "Humidité élevée = risque de mildiou. " +
                        "Évitez de mouiller les feuilles. Traitez préventivement au cuivre.";
            return "Pincez les gourmands régulièrement. " +
                    "Arrosage régulier au goutte-à-goutte recommandé. " +
                    "Tuteurez si nécessaire.";
        }

        if (c.contains("olive") || c.contains("olivier")) {
            if (saison == 1) // Printemps
                return "Période de floraison. Évitez les traitements chimiques. " +
                        "Surveillance de la mouche de l'olive recommandée.";
            if (saison == 2) // Été
                return "Irrigation d'appoint conseillée en période sèche " +
                        "pour améliorer le rendement. Surveillez la cochenille.";
            if (saison == 3) // Automne
                return "Période de récolte (octobre-décembre). " +
                        "Récoltez à véraison pour l'huile, à maturité complète pour la table.";
            return "Période de taille recommandée. " +
                    "Retirez les branches mortes et aérez la couronne.";
        }

        if (c.contains("agrume") || c.contains("orange")
                || c.contains("citron") || c.contains("clément")) {
            if (meteo != null && meteo.temperature < 5)
                return "⚠️ Risque de gel ! Les agrumes sont sensibles au froid. " +
                        "Protégez avec des voiles ou allumez des chaufferettes.";
            if (saison == 1)
                return "Période de floraison. Apport en potassium recommandé. " +
                        "Irrigation régulière indispensable.";
            return "Maintenir une irrigation régulière (900-1200mm/an). " +
                    "Fumure en 3 apports : février, mai et août.";
        }

        if (c.contains("pomme de terre") || c.contains("pomme")) {
            if (meteo != null && meteo.humidite > 80)
                return "Humidité élevée = risque de mildiou de la pomme de terre. " +
                        "Traitement fongicide préventif recommandé immédiatement.";
            return "Butage recommandé pour protéger les tubercules. " +
                    "Arrosage régulier, surtout au stade de tubérisation.";
        }

        if (c.contains("oignon") || c.contains("ail")) {
            if (saison == 1)
                return "Période de développement des bulbes. " +
                        "Réduisez progressivement l'arrosage. Surveillez le mildiou.";
            return "Arrosage modéré. Évitez l'excès d'humidité " +
                    "qui favorise la pourriture des bulbes.";
        }

        // Culture non reconnue
        return "Consultez la fiche de votre culture dans la Base de connaissances " +
                "pour des conseils personnalisés.";
    }

    // Conseil selon la saison
    private static String conseilSaisonnier(int saison) {
        switch (saison) {
            case 0: return "📅 Hiver : Période idéale pour la taille, " +
                    "l'amendement du sol et la planification de la saison.";
            case 1: return "📅 Printemps : Saison active ! Semis, plantation, " +
                    "premiers traitements préventifs.";
            case 2: return "📅 Été : Surveillez l'irrigation et protégez " +
                    "vos cultures de la chaleur excessive.";
            case 3: return "📅 Automne : Préparez les semis de céréales, " +
                    "récolte des olives et agrumes.";
            default: return "";
        }
    }

    // Retourne la saison actuelle (0=Hiver, 1=Printemps, 2=Été, 3=Automne)
    public static int getSaison() {
        int mois = Calendar.getInstance().get(Calendar.MONTH);
        if (mois >= 2 && mois <= 4) return 1;  // Mars-Mai
        if (mois >= 5 && mois <= 7) return 2;  // Juin-Août
        if (mois >= 8 && mois <= 10) return 3; // Sept-Nov
        return 0;                               // Déc-Fév
    }

    // Version courte pour le tableau de bord
    public static String conseilCourt(Meteo meteo, String culture) {
        if (meteo == null) return "💡 Consultez la météo du jour.";

        if (meteo.temperature > 35)
            return "🌡️ Chaleur ! Arrosez " + (culture.isEmpty() ? "tôt" :
                    "vos " + culture) + " avant 8h.";
        if (meteo.description != null && meteo.description.contains("pluie"))
            return "🌧️ Pluie prévue. Pas d'irrigation nécessaire.";
        if (meteo.humidite > 85)
            return "💧 Humidité élevée. Surveillez " +
                    (culture.isEmpty() ? "vos plantes." : "vos " + culture + ".");
        if (meteo.vitesseVent > 30)
            return "💨 Vent fort. Évitez les traitements.";
        if (meteo.temperature < 5)
            return "🥶 Risque gel ! Protégez " +
                    (culture.isEmpty() ? "vos cultures." : "vos " + culture + ".");

        return "✅ Bonne journée pour travailler " +
                (culture.isEmpty() ? "!" : "vos " + culture + " !");
    }
}