package com.example.tryproject.data;

import com.example.tryproject.model.FicheCulture;
import java.util.ArrayList;
import java.util.List;

public class FichesData {

    public static List<FicheCulture> getToutes() {
        List<FicheCulture> fiches = new ArrayList<>();

        fiches.add(new FicheCulture(
                "Blé",
                "🌾",
                "Céréale la plus cultivée au Maroc. Adaptée aux plaines et aux zones semi-arides.",
                "Octobre à Novembre (semis d'automne recommandé)",
                "400 à 500 mm par saison. Arrosage critique au stade tallage et épiaison.",
                "Azote (N) en deux apports : au semis et au tallage. Phosphore au semis.",
                "Rouille jaune, rouille brune, mildiou. Traiter dès les premiers symptômes.",
                "Récolte en juin-juillet. Moissonner quand le grain atteint 14% d'humidité."
        ));

        fiches.add(new FicheCulture(
                "Olivier",
                "🫒",
                "Arbre emblématique du Maroc, très résistant à la sécheresse. Longue durée de vie.",
                "Plantation : novembre à mars (période de repos végétatif)",
                "Résistant à la sécheresse. Irrigation d'appoint en été améliore le rendement de 40%.",
                "Fumure organique en automne. Azote au printemps avant la floraison.",
                "Œil de paon (taches circulaires sur feuilles), verticilliose, mouche de l'olive.",
                "Récolte octobre à décembre selon variété. Pour huile : cueillette à véraison."
        ));

        fiches.add(new FicheCulture(
                "Tomate",
                "🍅",
                "Culture maraîchère très répandue. Demande beaucoup de soin mais très rentable.",
                "Semis en pépinière : janvier-février. Transplantation : mars-avril.",
                "Arrosage régulier indispensable. Goutte-à-goutte recommandé. Éviter de mouiller les feuilles.",
                "NPK équilibré. Apport en calcium pour éviter la nécrose apicale.",
                "Mildiou, alternaria, mouche blanche, botrytis. Surveiller régulièrement.",
                "Récolte 70-90 jours après transplantation. Cueillir à maturité complète."
        ));

        fiches.add(new FicheCulture(
                "Agrumes",
                "🍊",
                "Oranges, citrons, clémentines. Culture importante dans les régions de Souss et Gharb.",
                "Plantation en automne ou au printemps. Pas de semis direct.",
                "Besoins élevés en eau : 900-1200 mm/an. Irrigation régulière obligatoire.",
                "Fumure complète en 3 apports : février, mai et août.",
                "Cochenilles, mouche méditerranéenne, alternariose. Traitement préventif conseillé.",
                "Selon variété : novembre (clémentine), décembre-février (orange)."
        ));

        fiches.add(new FicheCulture(
                "Pomme de terre",
                "🥔",
                "Culture très répandue dans les régions de Larache, Kenitra et Meknès.",
                "Deux saisons : janvier-février (printemps) ou août-septembre (automne).",
                "Besoins importants : 500-700 mm. Arrosage régulier, surtout à la tubérisation.",
                "Fumier bien décomposé + engrais NPK. Apport azoté fractionné.",
                "Mildiou (ennemi principal), alternaria, rhizoctone. Traitement fongicide préventif.",
                "90-120 jours après plantation. Arrêter l'irrigation 15 jours avant récolte."
        ));

        fiches.add(new FicheCulture(
                "Oignon",
                "🧅",
                "Culture maraîchère très répandue. Bonne valeur commerciale sur les marchés locaux.",
                "Semis : septembre-octobre. Transplantation : novembre-décembre.",
                "Modéré : 350-500 mm. Réduire progressivement avant récolte.",
                "Phosphore important pour le développement des bulbes. Azote en début de croissance.",
                "Mildiou, botrytis, thrips. Rotation des cultures recommandée.",
                "Récolte quand les feuilles tombent naturellement. Sécher avant stockage."
        ));

        return fiches;
    }
}
