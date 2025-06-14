package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class AjoutSoumissionActivity extends AppCompatActivity {

    private Spinner spinnerTravaux;
    private EditText editTextDescription;
    private Button btnAjouter;
    private String userId, prenomClient, email, adresse, telephone;

    FirebaseFirestore db;

    // ✅ Méthode de support multilingue
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_soumission);

        db = FirebaseFirestore.getInstance();

        spinnerTravaux = findViewById(R.id.spinnerTravaux);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnAjouter = findViewById(R.id.buttonAjouterSoumission);

        userId = getIntent().getStringExtra("userId");
        prenomClient = getIntent().getStringExtra("prenom");
        email = getIntent().getStringExtra("email");
        adresse = getIntent().getStringExtra("adresse");
        telephone = getIntent().getStringExtra("telephone");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.travaux_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTravaux.setAdapter(adapter);

        btnAjouter.setOnClickListener(v -> {
            String typeTravaux = spinnerTravaux.getSelectedItem().toString().trim();
            String description = editTextDescription.getText().toString();

            if (description.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_description), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> soumission = new HashMap<>();
            soumission.put("typeTravaux", typeTravaux);
            soumission.put("description", description);
            soumission.put("date", Timestamp.now());
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            soumission.put("clientId", userId);
            soumission.put("clientPrenom", prenomClient);
            soumission.put("clientEmail", email);
            soumission.put("clientAdresse", adresse);
            soumission.put("clientTelephone", telephone);
            soumission.put("lectureGlobale", true);

            db.collection("soumissions")
                    .add(soumission)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, getString(R.string.success_ajout_soumission), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, SoumissionsActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("prenom", prenomClient);
                        intent.putExtra("email", email);
                        intent.putExtra("adresse", adresse);
                        intent.putExtra("telephone", telephone);
                        intent.putExtra("role", "client");
                        intent.putExtra("specialite", typeTravaux);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, getString(R.string.error_ajout_soumission), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}


