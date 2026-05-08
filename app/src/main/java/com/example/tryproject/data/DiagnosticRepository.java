package com.example.tryproject.data;

import android.graphics.Bitmap;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiagnosticRepository {

    private static final String API_KEY = "AIzaSyDJCOSFrkX6BS70kIGrNXOHXI5rLuFG1eY";

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key="
                    + API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    public interface DiagnosticCallback {
        void onDiagnostic(String resultat);
    }

    public void analyserPhoto(Bitmap photo,
                              DiagnosticCallback callback) {

        try {

            /* CONVERTIR IMAGE EN BASE64 */
            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            photo.compress(
                    Bitmap.CompressFormat.JPEG,
                    80,
                    baos
            );

            byte[] imageBytes = baos.toByteArray();

            String base64Image =
                    Base64.encodeToString(
                            imageBytes,
                            Base64.NO_WRAP
                    );

            /* PROMPT IA */
            String prompt =
                    String prompt =
                    "Tu es AgroSmart MA, un assistant agricole marocain expert en maladies des plantes. " +

                            "Analyse cette photo agricole avec précision. " +

                            "Réponds TOUJOURS en français simple, clair et adapté au mobile. " +

                            "N'utilise JAMAIS de markdown comme ** ou ###. " +

                            "Utilise uniquement des emojis et des listes simples. " +

                            "Structure toujours la réponse exactement comme ceci :\n\n" +

                            "🔍 Diagnostic :\n" +
                            "Nom probable de la maladie ou du problème.\n\n" +

                            "⚠️ Gravité :\n" +
                            "Faible, modérée ou élevée.\n\n" +

                            "💡 Conseils :\n" +
                            "• Conseil 1\n" +
                            "• Conseil 2\n" +
                            "• Conseil 3\n\n" +

                            "💊 Traitement :\n" +
                            "• Traitement 1\n" +
                            "• Traitement 2\n\n" +

                            "🌱 Prévention :\n" +
                            "• Prévention 1\n" +
                            "• Prévention 2\n\n" +

                            "Les réponses doivent être courtes et très lisibles sur smartphone.";

            /* TEXTE */
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            /* IMAGE */
            JSONObject inlineData = new JSONObject();
            inlineData.put("mime_type", "image/jpeg");
            inlineData.put("data", base64Image);

            JSONObject imagePart = new JSONObject();
            imagePart.put("inline_data", inlineData);

            /* PARTS */
            JSONArray parts = new JSONArray();
            parts.put(textPart);
            parts.put(imagePart);

            /* CONTENT */
            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            /* BODY */
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

            client.newCall(request)
                    .enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call,
                                              IOException e) {

                            callback.onDiagnostic(
                                    "❌ Erreur : "
                                            + e.getMessage()
                            );
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

                                callback.onDiagnostic(
                                        "❌ Erreur API : "
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

                                String resultat =
                                        json.getJSONArray("candidates")
                                                .getJSONObject(0)
                                                .getJSONObject("content")
                                                .getJSONArray("parts")
                                                .getJSONObject(0)
                                                .getString("text");

                                callback.onDiagnostic(resultat);

                            } catch (Exception e) {

                                callback.onDiagnostic(
                                        "❌ Erreur analyse : "
                                                + e.getMessage()
                                );
                            }
                        }
                    });

        } catch (Exception e) {

            callback.onDiagnostic(
                    "❌ Erreur : "
                            + e.getMessage()
            );
        }
    }
}