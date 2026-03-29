package com.example.tryproject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.tryproject.model.Agriculteur;

// @Dao = Data Access Object : c'est ici qu'on dit quoi faire avec les données
@Dao
public interface AgriculteurDao {

    // Sauvegarder le profil (si il existe déjà, on le remplace)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void sauvegarder(Agriculteur agriculteur);

    // Lire le profil sauvegardé
    @Query("SELECT * FROM agriculteur WHERE id = 1")
    Agriculteur charger();
}