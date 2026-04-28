package com.example.tryproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tryproject.data.ChatbotRepository;
import com.example.tryproject.data.DiagnosticRepository;
import com.example.tryproject.model.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private static final int REQUEST_CAMERA     = 100;
    private static final int REQUEST_PERMISSION = 101;

    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private TextView txtLoading;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private ChatbotRepository repository;
    private DiagnosticRepository diagnosticRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        recyclerMessages = view.findViewById(R.id.recycler_messages);
        editMessage      = view.findViewById(R.id.edit_message);
        txtLoading       = view.findViewById(R.id.txt_loading);
        Button btnEnvoyer = view.findViewById(R.id.btn_envoyer);
        TextView btnPhoto = view.findViewById(R.id.btn_photo);

        adapter = new MessageAdapter(messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMessages.setAdapter(adapter);

        repository           = new ChatbotRepository();
        diagnosticRepository = new DiagnosticRepository();

        ajouterMessage(getString(R.string.bienvenue_chatbot), Message.AUTEUR_BOT);

        btnEnvoyer.setOnClickListener(v -> envoyerMessage());

        btnPhoto.setOnClickListener(v -> ouvrirCamera());

        return view;
    }

    private void ouvrirCamera() {
        // Vérifier permission caméra
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            return;
        }

        // Ouvrir la caméra
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ouvrirCamera();
        } else {
            ajouterMessage("❌ Permission caméra refusée.", Message.AUTEUR_BOT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA
                && resultCode == Activity.RESULT_OK
                && data != null) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Message de l'utilisateur avec indication photo
            ajouterMessage("📷 Photo envoyée pour analyse...", Message.AUTEUR_USER);
            txtLoading.setVisibility(View.VISIBLE);

            // Analyser la photo
            diagnosticRepository.analyserPhoto(photo, diagnostic ->
                    requireActivity().runOnUiThread(() -> {
                        txtLoading.setVisibility(View.GONE);
                        ajouterMessage("🔬 Résultat de l'analyse :\n\n" + diagnostic,
                                Message.AUTEUR_BOT);
                    })
            );
        }
    }

    private void envoyerMessage() {
        String texte = editMessage.getText().toString().trim();
        if (texte.isEmpty()) return;

        ajouterMessage(texte, Message.AUTEUR_USER);
        editMessage.setText("");
        txtLoading.setVisibility(View.VISIBLE);

        repository.envoyerMessage(messages, new ChatbotRepository.ReponseCallback() {
            @Override
            public void onReponse(String reponse) {
                requireActivity().runOnUiThread(() -> {
                    txtLoading.setVisibility(View.GONE);
                    ajouterMessage(reponse, Message.AUTEUR_BOT);
                });
            }

            @Override
            public void onErreur(String erreur) {
                requireActivity().runOnUiThread(() -> {
                    txtLoading.setVisibility(View.GONE);
                    ajouterMessage("Erreur : " + erreur, Message.AUTEUR_BOT);
                });
            }
        });
    }

    private void ajouterMessage(String texte, String auteur) {
        messages.add(new Message(texte, auteur));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerMessages.scrollToPosition(messages.size() - 1);
    }

    // ===== ADAPTER =====
    static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<Message> messages;

        MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Message msg = messages.get(position);
            boolean estUser = msg.auteur.equals(Message.AUTEUR_USER);

            if (estUser) {
                holder.bulleUser.setVisibility(View.VISIBLE);
                holder.bulleBot.setVisibility(View.GONE);
                holder.txtUser.setText(msg.texte);
            } else {
                holder.bulleBot.setVisibility(View.VISIBLE);
                holder.bulleUser.setVisibility(View.GONE);
                holder.txtBot.setText(msg.texte);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout bulleUser, bulleBot;
            TextView txtUser, txtBot;

            ViewHolder(View v) {
                super(v);
                bulleUser = v.findViewById(R.id.bulle_user);
                bulleBot  = v.findViewById(R.id.bulle_bot);
                txtUser   = v.findViewById(R.id.txt_message_user);
                txtBot    = v.findViewById(R.id.txt_message_bot);
            }
        }
    }
}
