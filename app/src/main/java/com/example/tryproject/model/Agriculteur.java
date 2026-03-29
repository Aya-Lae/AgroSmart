package com.example.tryproject.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity dit à Room : "cette classe = une table dans la base de données"
@Entity(tableName = "agriculteur")
public class Agriculteur {

    @PrimaryKey
    public int id = 1; // on aura toujours un seul profil, donc id fixe à 1

    public String nom;
    public String region;
    public String culture;
    public String langue; // "fr" ou "ar"
}