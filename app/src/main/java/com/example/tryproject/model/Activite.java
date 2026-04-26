package com.example.tryproject.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activite")
public class Activite {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String type;      // "Semis", "Irrigation", "Fertilisation", "Récolte", "Traitement"
    public String culture;   // "Blé", "Tomate"...
    public String date;      // format "JJ/MM/AAAA"
    public String note;      // remarque optionnelle
    public boolean faite;    // true = activité accomplie
}