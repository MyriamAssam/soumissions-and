package com.example.app_soumission;

import java.util.List;

public class SoumissionRequest {
    private String adresse;
    private String prenomClient;
    private String nomEmployeur; // Optionnel
    private String email;
    private String description;
    private String telephone;
    private String employeurId; // Optionnel
    private String clientId;
    private List<String> travaux;

    public SoumissionRequest(String adresse, String prenomClient, String nomEmployeur,
                             String email, String description, String telephone,
                             String employeurId, String clientId, List<String> travaux) {
        this.adresse = adresse;
        this.prenomClient = prenomClient;
        this.nomEmployeur = nomEmployeur;
        this.email = email;
        this.description = description;
        this.telephone = telephone;
        this.employeurId = employeurId;
        this.clientId = clientId;
        this.travaux = travaux;
    }
}


