package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

import retrofit2.Call;

public class SoumInfosActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private String clientId;
 
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

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

        Button btnall = findViewById(R.id.button16);


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

        String employeurId = intent.getStringExtra("employeurId");

// existing prefs
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String currentUserId = prefs.getString("userId", "");
        String currentRole = prefs.getString("role", "");

        if ("employé".equals(currentRole)) {
            btn_soumisModi.setVisibility(View.GONE);
            btn_soumisAdd.setVisibility(View.GONE);

            String specialite = prefs.getString("specialite", "");

            // Check if this soumission's travaux includes the employee's specialite
            List<String> travauxList = Arrays.asList(travauxArray != null ? travauxArray : new String[]{});
            if (!travauxList.contains(specialite)) {
                btnSupprimer.setVisibility(View.GONE); // hide delete if not their specialty
            }
        } else if ("client".equals(currentRole)) {
            if (!currentUserId.equals(clientId)) {
                btn_soumisModi.setVisibility(View.GONE);
                btnSupprimer.setVisibility(View.GONE);
            }
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
            String token = prefs.getString("token", "");
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            apiService.deleteSoumission(soumissionId, "Bearer " + token)
                    .enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(SoumInfosActivity.this, getString(R.string.soumission_supprimee), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SoumInfosActivity.this, SoumissionsActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SoumInfosActivity.this, getString(R.string.erreur), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SoumInfosActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });




        btnDeconnexion.setOnClickListener(v -> {
            Intent intent5 = new Intent(this, MainActivity.class);
            intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent5);
            finish();
        });
    }
}

