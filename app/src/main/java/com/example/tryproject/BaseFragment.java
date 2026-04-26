package com.example.tryproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tryproject.data.FichesData;
import com.example.tryproject.model.FicheCulture;
import java.util.List;

public class BaseFragment extends Fragment {

    private RecyclerView recyclerFiches;
    private ScrollView vueDetail;
    private List<FicheCulture> fiches;

    // Vues du détail
    private TextView detailTitre, detailDescription;
    private TextView[] sectionTitres   = new TextView[5];
    private TextView[] sectionContenus = new TextView[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);

        recyclerFiches  = view.findViewById(R.id.recycler_fiches);
        vueDetail       = view.findViewById(R.id.vue_detail);
        detailTitre     = view.findViewById(R.id.detail_titre);
        detailDescription = view.findViewById(R.id.detail_description);
        Button btnRetour  = view.findViewById(R.id.btn_retour);

        // Récupérer les sections dynamiquement
        int[] sectionIds = {R.id.section1, R.id.section2, R.id.section3,
                R.id.section4, R.id.section5};
        for (int i = 0; i < 5; i++) {
            View s = view.findViewById(sectionIds[i]);
            sectionTitres[i]   = s.findViewById(R.id.section_titre);
            sectionContenus[i] = s.findViewById(R.id.section_contenu);
        }

        fiches = FichesData.getToutes();
        FicheAdapter adapter = new FicheAdapter(fiches, this::afficherDetail);
        recyclerFiches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFiches.setAdapter(adapter);

        btnRetour.setOnClickListener(v -> {
            vueDetail.setVisibility(View.GONE);
            recyclerFiches.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void afficherDetail(FicheCulture fiche) {
        recyclerFiches.setVisibility(View.GONE);
        vueDetail.setVisibility(View.VISIBLE);

        detailTitre.setText(fiche.emoji + " " + fiche.nom);
        detailDescription.setText(fiche.description);

        String[] titres = {
                "📅 Période de semis",
                "💧 Irrigation",
                "🌱 Fertilisation",
                "🦠 Maladies fréquentes",
                "🌾 Récolte"
        };
        String[] contenus = {
                fiche.periodesSemis,
                fiche.irrigation,
                fiche.fertilisation,
                fiche.maladiesFrequentes,
                fiche.conseilsRecolte
        };

        for (int i = 0; i < 5; i++) {
            sectionTitres[i].setText(titres[i]);
            sectionContenus[i].setText(contenus[i]);
        }
    }

    // ===== ADAPTER =====
    static class FicheAdapter extends RecyclerView.Adapter<FicheAdapter.ViewHolder> {

        interface OnFicheClick { void onClick(FicheCulture fiche); }

        private List<FicheCulture> fiches;
        private OnFicheClick listener;

        FicheAdapter(List<FicheCulture> fiches, OnFicheClick listener) {
            this.fiches   = fiches;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_fiche, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FicheCulture fiche = fiches.get(position);
            holder.emoji.setText(fiche.emoji);
            holder.nom.setText(fiche.nom);
            holder.description.setText(fiche.description);
            holder.itemView.setOnClickListener(v -> listener.onClick(fiche));
        }

        @Override
        public int getItemCount() { return fiches.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView emoji, nom, description;
            ViewHolder(View v) {
                super(v);
                emoji       = v.findViewById(R.id.item_emoji);
                nom         = v.findViewById(R.id.item_nom);
                description = v.findViewById(R.id.item_description);
            }
        }
    }
}