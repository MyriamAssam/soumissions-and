package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class SoumInfosActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private String clientId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    // ✅ ✅ ✅  AJOUTE-LA ICI (en dehors de onCreate)
    private void setRowContent(int rowId, String labelText, String valueText) {
        View row = findViewById(rowId);
        TextView label = row.findViewById(R.id.label);
        TextView value = row.findViewById(R.id.value);
        label.setText(labelText);
        value.setText(valueText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soum_infos);

        db = FirebaseFirestore.getInstance();

        Button btn_soumis = findViewById(R.id.btn_soumis);
        Button btnDeconnexion = findViewById(R.id.btn_deconnexion);
        Button btn_soumisModi = findViewById(R.id.btn_soumisModi);
        Button btn_soumisAdd = findViewById(R.id.btn_soumisAdd);
        Button btnSupprimer = findViewById(R.id.btn_supprimer);
        Button btnVoir = findViewById(R.id.btn_voir);
        Button btnSauvegarder = findViewById(R.id.button13);
        Button btnall = findViewById(R.id.button16);
        EditText etNote = findViewById(R.id.et_notes);

        Intent intent = getIntent();
        String soumissionId = intent.getStringExtra("soumissionId");
        String role = intent.getStringExtra("role");
        clientId = intent.getStringExtra("clientId");

        String prenom = intent.getStringExtra("prenom");
        String email = intent.getStringExtra("email");
        String adresse = intent.getStringExtra("adresse");
        String telephone = intent.getStringExtra("telephone");
        String description = intent.getStringExtra("description");

        String[] travauxArray = intent.getStringArrayExtra("travaux");
        String travaux = (travauxArray != null && travauxArray.length > 0)
                ? String.join(", ", travauxArray)
                : getString(R.string.aucun_travaux);

        // ✅ Affichage dynamique avec alignement propre
        setRowContent(R.id.row_prenom, getString(R.string.prenom_label), prenom);
        setRowContent(R.id.row_email, getString(R.string.email_label), email);
        setRowContent(R.id.row_adresse, getString(R.string.adresse_label), adresse);
        setRowContent(R.id.row_telephone, getString(R.string.telephone_label), telephone);
        setRowContent(R.id.row_description, getString(R.string.description_label), description);
        setRowContent(R.id.row_travaux, getString(R.string.travaux_label), travaux);

        Object raw = intent.getSerializableExtra("date");
        if (raw instanceof Date) {
            Date date = (Date) raw;
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(
                    java.text.DateFormat.MEDIUM,
                    java.text.DateFormat.SHORT,
                    Locale.getDefault()
            );
            setRowContent(R.id.row_date, getString(R.string.date_label), dateFormat.format(date));
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String currentRole = prefs.getString("role", "");

        if (currentRole.equals("employé")) {
            btn_soumisModi.setVisibility(View.GONE);
            btn_soumisAdd.setVisibility(View.GONE);
        }

        btnVoir.setOnClickListener(v -> {
            Intent intentVoir = new Intent(SoumInfosActivity.this, NotesActivity.class);
            intentVoir.putExtra("soumissionId", soumissionId);
            intentVoir.putExtra("role", role);
            intentVoir.putExtra("clientId", clientId);
            startActivity(intentVoir);
        });

        btn_soumis.setOnClickListener(v -> startActivity(new Intent(this, SoumissionsActivity.class)));
        btnall.setOnClickListener(v -> startActivity(new Intent(this, AllSoumissionsActivity.class)));

        btn_soumisModi.setOnClickListener(v -> {
            Intent intent10 = new Intent(this, ModiSoumissionActivity.class);
            intent10.putExtra("soumissionId", soumissionId);
            intent10.putExtra("description", description);
            intent10.putExtra("travaux", travaux);
            startActivity(intent10);
        });

        btn_soumisAdd.setOnClickListener(v -> startActivity(new Intent(this, AjoutSoumissionActivity.class)));

        btnSupprimer.setOnClickListener(v -> {
            db.collection("soumissions").document(soumissionId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.soumission_supprimee), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, SoumissionsActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.erreur) + " " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnSauvegarder.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            if (note.isEmpty()) {
                Toast.makeText(this, getString(R.string.note_vide), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> noteData = new HashMap<>();
            noteData.put("note", note);
            noteData.put("role", role);
            noteData.put("timestamp", Timestamp.now());
            noteData.put("auteurId", FirebaseAuth.getInstance().getUid());

            String auteur = prefs.getString("prenom", "Inconnu");
            noteData.put("auteur", auteur);

            db.collection("soumissions")
                    .document(soumissionId)
                    .collection("notes")
                    .add(noteData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, getString(R.string.note_sauvegardee), Toast.LENGTH_SHORT).show();
                        etNote.setText("");
                        Intent intentVoir = new Intent(SoumInfosActivity.this, NotesActivity.class);
                        intentVoir.putExtra("soumissionId", soumissionId);
                        intentVoir.putExtra("role", role);
                        intentVoir.putExtra("prenom", prenom);
                        intentVoir.putExtra("clientId", clientId);
                        startActivity(intentVoir);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.erreur) + " " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnDeconnexion.setOnClickListener(v -> {
            Intent intent5 = new Intent(this, MainActivity.class);
            intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent5);
            finish();
        });
    }
}
