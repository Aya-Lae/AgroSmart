package com.example.tryproject.data;

import com.example.tryproject.model.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotRepository {

    private static final String API_KEY = "AIzaSyDJCOSFrkX6BS70kIGrNXOHXI5rLuFG1eY";

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key="
                    + API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    public interface ReponseCallback {
        void onReponse(String reponse);
        void onErreur(String erreur);
    }

    public void envoyerMessage(List<Message> historique,
                               ReponseCallback callback) {

        try {

            JSONArray contents = new JSONArray();

            /* PERSONNALITÉ AGROSMART */
            JSONObject systemPart = new JSONObject();

            systemPart.put(
                    "text",
                    "\"Tu es AgroSmart, un assistant agricole intelligent spécialisé en agriculture. \" +\n" +
                            "\"Tu aides les agriculteurs avec les cultures, maladies, irrigation, météo et fertilisation. \" +\n" +
                            "\"Réponds toujours en français. \" +\n" +
                            "\"Fais des réponses courtes, claires et adaptées au mobile. \" +\n" +
                            "\"Utilise des listes simples avec des emojis. \" +\n" +
                            "\"N'utilise jamais de markdown comme ### ou **. \" +\n" +
                            "\"Donne des conseils pratiques et faciles à comprendre.\""
            );

            JSONArray systemParts = new JSONArray();
            systemParts.put(systemPart);

            JSONObject systemContent = new JSONObject();
            systemContent.put("role", "user");
            systemContent.put("parts", systemParts);

            contents.put(systemContent);

            /* HISTORIQUE COMPLET */
            for (Message msg : historique) {

                JSONObject part = new JSONObject();
                part.put("text", msg.texte);

                JSONArray parts = new JSONArray();
                parts.put(part);

                JSONObject content = new JSONObject();

                if (msg.auteur.equals(Message.AUTEUR_USER)) {
                    content.put("role", "user");
                } else {
                    content.put("role", "model");
                }

                content.put("parts", parts);

                contents.put(content);
            }

            JSONObject body = new JSONObject();
            body.put("contents", contents);

            RequestBody requestBody =
                    RequestBody.create(
                            body.toString(),
                            MediaType.get("application/json")
                    );

            Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onErreur(e.getMessage());
                }

                @Override
                public void onResponse(Call call,
                                       Response response)
                        throws IOException {

                    if (!response.isSuccessful()) {

                        String erreur =
                                response.body() != null
                                        ? response.body().string()
                                        : "Erreur inconnue";

                        callback.onErreur(
                                "Erreur API : "
                                        + response.code()
                                        + "\n"
                                        + erreur
                        );

                        return;
                    }

                    try {

                        String responseBody =
                                response.body().string();

                        JSONObject json =
                                new JSONObject(responseBody);

                        String texte =
                                json.getJSONArray("candidates")
                                        .getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text");

                        callback.onReponse(texte);

                    } catch (Exception e) {
                        callback.onErreur(e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onErreur(e.getMessage());
        }
    }
}