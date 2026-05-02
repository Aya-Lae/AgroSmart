package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tryproject.data.AgricoDatabase;
import com.example.tryproject.model.Activite;
import java.util.ArrayList;
import java.util.List;

public class CalendrierFragment extends Fragment {

    private RecyclerView recyclerActivites;
    private EditText editCulture, editType, editDate, editNote;
    private List<Activite> activites = new ArrayList<>();
    private ActiviteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendrier, container, false);

        recyclerActivites = view.findViewById(R.id.recycler_activites);
        editCulture = view.findViewById(R.id.edit_culture);
        editType    = view.findViewById(R.id.edit_type);
        editDate    = view.findViewById(R.id.edit_date);
        editNote    = view.findViewById(R.id.edit_note);

        com.google.android.material.button.MaterialButton btnAjouter =
                view.findViewById(R.id.btn_ajouter);

        // Adapter avec 2 listeners : marquer faite + supprimer
        adapter = new ActiviteAdapter(
                activites,
                this::marquerFaite,
                this::supprimerActivite   // ← nouveau
        );
        recyclerActivites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerActivites.setAdapter(adapter);

        chargerActivites();

        btnAjouter.setOnClickListener(v -> ajouterActivite());

        return view;
    }

    private void ajouterActivite() {
        String culture = editCulture.getText().toString().trim();
        String type    = editType.getText().toString().trim();
        String date    = editDate.getText().toString().trim();
        String note    = editNote.getText().toString().trim();

        if (culture.isEmpty() || type.isEmpty() || date.isEmpty()) return;

        new Thread(() -> {
            Activite a = new Activite();
            a.culture = culture;
            a.type    = type;
            a.date    = date;
            a.note    = note;
            a.faite   = false;

            AgricoDatabase.getInstance(getContext()).activiteDao().ajouter(a);

            requireActivity().runOnUiThread(() -> {
                editCulture.setText("");
                editType.setText("");
                editDate.setText("");
                editNote.setText("");
                chargerActivites();
            });
        }).start();
    }

    private void chargerActivites() {
        new Thread(() -> {
            List<Activite> liste = AgricoDatabase.getInstance(getContext())
                    .activiteDao().getTout();
            requireActivity().runOnUiThread(() -> {
                activites.clear();
                activites.addAll(liste);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void marquerFaite(Activite activite) {
        new Thread(() -> {
            activite.faite = !activite.faite;
            AgricoDatabase.getInstance(getContext()).activiteDao().modifier(activite);
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    // ← NOUVELLE MÉTHODE
    private void supprimerActivite(Activite activite) {
        new Thread(() -> {
            AgricoDatabase.getInstance(getContext())
                    .activiteDao().supprimer(activite.id);
            requireActivity().runOnUiThread(() -> chargerActivites());
        }).start();
    }

    // ═══════════════ ADAPTER ═══════════════
    static class ActiviteAdapter extends RecyclerView.Adapter<ActiviteAdapter.ViewHolder> {

        interface OnCheck  { void onClick(Activite activite); }
        interface OnDelete { void onClick(Activite activite); } // ← nouveau

        private List<Activite> activites;
        private final OnCheck  listenerCheck;
        private final OnDelete listenerDelete; // ← nouveau

        ActiviteAdapter(List<Activite> activites,
                        OnCheck listenerCheck,
                        OnDelete listenerDelete) {
            this.activites      = activites;
            this.listenerCheck  = listenerCheck;
            this.listenerDelete = listenerDelete;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activite, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Activite a = activites.get(position);

            holder.titre.setText(a.type + " — " + a.culture);
            holder.date.setText(" " + a.date);
            holder.note.setText(a.note != null ? a.note : "");

            // Badge type
            TextView badge = holder.itemView.findViewById(R.id.item_badge_type);
            if (badge != null) badge.setText(a.type);

            // Checkbox
            holder.check.setChecked(a.faite);
            holder.titre.setPaintFlags(a.faite
                    ? holder.titre.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    : holder.titre.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.check.setOnClickListener(v -> listenerCheck.onClick(a));

            // ← MENU 3 POINTS
            holder.menuBtn.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add(0, 1, 0, "🗑️ Supprimer");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1) {
                        listenerDelete.onClick(a);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        @Override
        public int getItemCount() { return activites.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titre, date, note;
            CheckBox check;
            ImageView menuBtn; // ← nouveau

            ViewHolder(View v) {
                super(v);
                titre   = v.findViewById(R.id.item_titre);
                date    = v.findViewById(R.id.item_date);
                note    = v.findViewById(R.id.item_note);
                check   = v.findViewById(R.id.check_faite);
                menuBtn = v.findViewById(R.id.item_menu); // ← nouveau
            }
        }
    }
}