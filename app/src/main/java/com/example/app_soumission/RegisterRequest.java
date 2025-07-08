package com.example.app_soumission;

public class RegisterRequest {
    private String prenom;
    private String email;
    private String mdp;
    private String adresse;
    private String telephone;
    private String role;
    private String specialite; // Peut rester null pour les clients

    public RegisterRequest(String prenom, String email, String mdp, String adresse,
                           String telephone, String role, String specialite) {
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
        this.telephone = telephone;
        this.role = role;
        this.specialite = specialite;
    }

    // Getters et setters (si nécessaires)
}
