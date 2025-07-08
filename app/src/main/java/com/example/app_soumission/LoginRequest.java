package com.example.app_soumission;

public class LoginRequest {
    private String email;
    private String mdp;
    private String type;

    public LoginRequest(String email, String mdp, String type) {
        this.email = email;
        this.mdp = mdp;
        this.type = type;
    }

    // Getters et setters si besoin
}
