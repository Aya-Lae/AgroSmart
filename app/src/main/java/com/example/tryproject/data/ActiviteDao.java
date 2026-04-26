package com.example.tryproject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.tryproject.model.Activite;
import java.util.List;

@Dao
public interface ActiviteDao {

    @Insert
    void ajouter(Activite activite);

    @Update
    void modifier(Activite activite);

    @Query("SELECT * FROM activite ORDER BY date ASC")
    List<Activite> getTout();

    @Query("DELETE FROM activite WHERE id = :id")
    void supprimer(int id);
}