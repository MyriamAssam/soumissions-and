package com.example.app_soumission;

import java.util.List;

public class NouvelleSoumission {
    private String adresse;
    private String prenomClient;
    private String nomEmployeur; // optionnel
    private String email;
    private String description;
    private String telephone;
    private String employeurId; // ne sera pas utilisé, peut rester vide
    private String clientId;
    private List<String> travaux;
    private String date;

    public NouvelleSoumission(String adresse, String prenomClient, String nomEmployeur,
                              String email, String description, String telephone,
                              String employeurId, String clientId,
                              List<String> travaux, String date) {
        this.adresse = adresse;
        this.prenomClient = prenomClient;
        this.nomEmployeur = nomEmployeur;
        this.email = email;
        this.description = description;
        this.telephone = telephone;
        this.employeurId = employeurId;
        this.clientId = clientId;
        this.travaux = travaux;
        this.date = date;
    }
}


