package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_soumission.utils.JwtUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.*;

import retrofit2.Call;

public class AjoutSoumissionActivity extends AppCompatActivity {

    private Spinner spinnerTravaux;
    private EditText editTextDescription;
    private Button btnAjouter;
    private String userId, prenomClient, email, adresse, telephone;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_soumission);

        spinnerTravaux = findViewById(R.id.spinnerTravaux);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnAjouter = findViewById(R.id.buttonAjouterSoumission);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Chargement depuis SharedPreferences
        userId = prefs.getString("userId", "");
        prenomClient = prefs.getString("prenom", "");
        email = prefs.getString("email", "");
        adresse = prefs.getString("adresse", "");
        telephone = prefs.getString("telephone", "");
        String token = prefs.getString("token", "");

        // Fallback: Intent si SharedPreferences sont vides
        Intent intent = getIntent();
        if ((prenomClient == null || prenomClient.isEmpty()) && intent.hasExtra("prenom")) {
            prenomClient = intent.getStringExtra("prenom");
            email = intent.getStringExtra("email");
            adresse = intent.getStringExtra("adresse");
            telephone = intent.getStringExtra("telephone");
            userId = intent.getStringExtra("userId");
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.travaux_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTravaux.setAdapter(adapter);

        btnAjouter.setOnClickListener(v -> {
            String typeTravaux = spinnerTravaux.getSelectedItem().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_description), Toast.LENGTH_SHORT).show();
                return;
            }

            if (prenomClient == null || prenomClient.isEmpty() ||
                    email == null || email.isEmpty() ||
                    adresse == null || adresse.isEmpty() ||
                    telephone == null || telephone.isEmpty()) {

                Toast.makeText(this, getString(R.string.error_missing_profile_info), Toast.LENGTH_LONG).show();
                return;
            }


            List<String> travaux = Collections.singletonList(typeTravaux);



            System.out.println("=== DONNÉES ENVOYÉES ===");
            System.out.println("adresse: " + adresse);
            System.out.println("prenomClient: " + prenomClient);
            System.out.println("email: " + email);
            System.out.println("telephone: " + telephone);

            System.out.println("travaux: " + travaux);

            SoumissionRequest request = new SoumissionRequest(
                    adresse,
                    prenomClient,
                    "",   // nomEmployeur si client
                    email,
                    description,
                    telephone,
                    "",   // employeurId si client
                    userId,   // clientId doit être présent !
                    travaux
            );
            System.out.println("=== DONNÉES ENVOYÉES ===");
            System.out.println("adresse: " + adresse);
            System.out.println("prenomClient: " + prenomClient);
            System.out.println("nomEmployeur: " + ""); // ou la variable correspondante
            System.out.println("email: " + email);
            System.out.println("telephone: " + telephone);
            System.out.println("clientId: " + userId);
            System.out.println("employeurId: " + "");
            System.out.println("travaux: " + travaux);
            System.out.println("TOKEN: " + token);
            System.out.println("USER ID: " + userId);



            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            apiService.addSoumission(request, "Bearer " + token)
                    .enqueue(new retrofit2.Callback<SoumissionResponse>() {
                        @Override
                        public void onResponse(Call<SoumissionResponse> call, retrofit2.Response<SoumissionResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AjoutSoumissionActivity.this, getString(R.string.success_ajout_soumission), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AjoutSoumissionActivity.this, SoumissionsActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AjoutSoumissionActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SoumissionResponse> call, Throwable t) {
                            Toast.makeText(AjoutSoumissionActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
