package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class ModiSoumissionActivity extends AppCompatActivity {

    private String soumissionId;
    private EditText etDescription;
    private Spinner spinnerTravaux;
    private FirebaseFirestore db;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification);

        db = FirebaseFirestore.getInstance();

        etDescription = findViewById(R.id.etDescription);
        spinnerTravaux = findViewById(R.id.spinner_travaux);
        Button btnModifier = findViewById(R.id.btn_modifier);
        Button btnRetour = findViewById(R.id.btn_retour);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.travaux_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTravaux.setAdapter(adapter);

        Intent intent = getIntent();
        soumissionId = intent.getStringExtra("soumissionId");
        String description = intent.getStringExtra("description");
        String travaux = intent.getStringExtra("travaux");

        etDescription.setText(description);
        if (travaux != null) {
            int position = adapter.getPosition(travaux);
            spinnerTravaux.setSelection(position);
        }

        btnRetour.setOnClickListener(v -> {
            Intent retourIntent = new Intent(ModiSoumissionActivity.this, SoumInfosActivity.class);
            retourIntent.putExtra("soumissionId", soumissionId);
            startActivity(retourIntent);
            finish();
        });

        btnModifier.setOnClickListener(v -> {
            String nouvelleDescription = etDescription.getText().toString();
            String nouveauTravaux = spinnerTravaux.getSelectedItem().toString();

            if (nouvelleDescription.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_description), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("description", nouvelleDescription);
            updates.put("typeTravaux", nouveauTravaux);

            db.collection("soumissions")
                    .document(soumissionId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.success_modification), Toast.LENGTH_SHORT).show();
                        Intent retourIntent = new Intent(ModiSoumissionActivity.this, SoumissionsActivity.class);
                        retourIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(retourIntent);
                        finish();
                    });
        });
    }
}

