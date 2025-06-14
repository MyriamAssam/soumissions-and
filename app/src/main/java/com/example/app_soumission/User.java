package com.example.app_soumission;

import com.google.gson.annotations.SerializedName;

public class User {
    private String prenom;
    private String email;

    @SerializedName("mdp")
    private String motDePasse;
    @SerializedName("_id")
    private String id;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    private String adresse;
    private String telephone;
    private String role;
    private String specialite;


    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setRole(String role) { this.role = role; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialite() {
        return specialite;
    }
}

