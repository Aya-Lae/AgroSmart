package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tryproject.data.ChatbotRepository;
import com.example.tryproject.model.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private TextView txtLoading;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private ChatbotRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        recyclerMessages = view.findViewById(R.id.recycler_messages);
        editMessage      = view.findViewById(R.id.edit_message);
        txtLoading       = view.findViewById(R.id.txt_loading);
        Button btnEnvoyer = view.findViewById(R.id.btn_envoyer);

        adapter = new MessageAdapter(messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMessages.setAdapter(adapter);

        repository = new ChatbotRepository();

        ajouterMessage("Bonjour ! Je suis AgroSmart. Comment puis-je vous aider aujourd'hui ?",
                Message.AUTEUR_BOT);

        btnEnvoyer.setOnClickListener(v -> envoyerMessage());

        return view;
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