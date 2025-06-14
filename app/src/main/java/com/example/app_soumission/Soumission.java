package com.example.app_soumission;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Soumission {
    private String prenomClient;
    private String nomEmployeur;
    private String email;
    private String description;

    @SerializedName("mdp")
    private String motDePasse;

    private String adresse;
    private String telephone;
    private List<String> travaux;

    private String employeurId;
    private String clientId;

    @SerializedName("_id")
    private String id;

    private List<Note> notesClients;
    private List<Note> notesEmployes;

    // --- Getters ---
    public String getId() { return id; }
    public String getPrenomClient() { return prenomClient; }
    public String getNomEmployeur() { return nomEmployeur; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
    public String getAdresse() { return adresse; }
    public String getTelephone() { return telephone; }
    public List<String> getTravaux() { return travaux; }
    private Date date;

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }


    public String getEmployeurId() { return employeurId; }
    public String getClientId() { return clientId; }
    public List<Note> getNotesClients() { return notesClients; }
    public List<Note> getNotesEmployes() { return notesEmployes; }

    // --- Setters ---
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }
    public void setNomEmployeur(String nomEmployeur) { this.nomEmployeur = nomEmployeur; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setTravaux(List<String> travaux) { this.travaux = travaux; }

    public void setEmployeurId(String employeurId) { this.employeurId = employeurId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setNotesClients(List<Note> notesClients) { this.notesClients = notesClients; }
    public void setNotesEmployes(List<Note> notesEmployes) { this.notesEmployes = notesEmployes; }


}
