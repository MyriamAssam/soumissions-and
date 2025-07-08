package com.example.app_soumission;

public class UserResponse {
    private String userId;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private String role;
    private String specialite; // seulement si présent dans d'autres cas
    private String token;
    private User user;

    // Getters
    public String getUserId() { return userId; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getAdresse() { return adresse; }
    public String getTelephone() { return telephone; }
    public String getRole() { return role; }
    public String getSpecialite() { return specialite; }
    public User getUser() { return user; }
    public String getToken() { return token; }
}
