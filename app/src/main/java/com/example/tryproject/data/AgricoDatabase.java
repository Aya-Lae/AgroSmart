package com.example.tryproject.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.tryproject.model.Agriculteur;

// On déclare quelles tables (entities) sont dans cette base
@Database(entities = {Agriculteur.class}, version = 1)
public abstract class AgricoDatabase extends RoomDatabase {

    private static AgricoDatabase instance;

    // Room va générer cette méthode automatiquement
    public abstract AgriculteurDao agriculteurDao();

    // Singleton : on crée la base une seule fois
    public static AgricoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AgricoDatabase.class,
                    "agrico_db"
            ).build();
        }
        return instance;
    }
}