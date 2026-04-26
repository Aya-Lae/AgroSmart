package com.example.tryproject.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.tryproject.model.Agriculteur;
import com.example.tryproject.model.Activite;

@Database(entities = {Agriculteur.class, Activite.class}, version = 2)
public abstract class AgricoDatabase extends RoomDatabase {

    private static AgricoDatabase instance;

    public abstract AgriculteurDao agriculteurDao();
    public abstract ActiviteDao activiteDao();

    public static AgricoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AgricoDatabase.class,
                            "agrico_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}