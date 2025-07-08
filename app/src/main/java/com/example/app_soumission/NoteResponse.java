package com.example.app_soumission;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NoteResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("auteur")
    private String auteur;

    @SerializedName("texte")
    private String note;

    @SerializedName("date")
    private Date timestamp;

    private String role; // Optionnel : tu peux le calculer côté app si besoin

    public String getId() {
        return id;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getNote() {
        return note;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
