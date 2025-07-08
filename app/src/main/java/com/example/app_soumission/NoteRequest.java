package com.example.app_soumission;

public class NoteRequest {
    private String id;      // UUID généré ou Firebase UID
    private String texte;
    // Texte de la note
    private String role;
    private String auteur;

    public NoteRequest(String id, String texte, String role, String auteur) {
        this.id = id;
        this.texte = texte;
        this.role = role;
        this.auteur = auteur;
    }



    // Getters et setters si nécessaires
}
