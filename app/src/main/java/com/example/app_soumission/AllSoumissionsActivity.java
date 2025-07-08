package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllSoumissionsActivity extends AppCompatActivity {

    Button buttonMesSoumissions, buttonDeconnexion;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_soumissions);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        String role = prefs.getString("role", "");

        buttonMesSoumissions = findViewById(R.id.button14);
        buttonDeconnexion = findViewById(R.id.button17);

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        apiService.getAllSoumissions("Bearer " + token)
                .enqueue(new Callback<SoumissionListResponse>() {
                    @Override
                    public void onResponse(Call<SoumissionListResponse> call, Response<SoumissionListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            afficherToutesSoumissions(response.body().getSoumissions(), role);
                        } else {
                            Toast.makeText(AllSoumissionsActivity.this, getString(R.string.erreur), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SoumissionListResponse> call, Throwable t) {
                        Toast.makeText(AllSoumissionsActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        buttonMesSoumissions.setText(getString(R.string.voir_mes_soumissions));
        buttonDeconnexion.setText(getString(R.string.btn_deconnexion));

        buttonMesSoumissions.setOnClickListener(v ->
                startActivity(new Intent(this, SoumissionsActivity.class)));

        buttonDeconnexion.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
    }

    private String getLocalizedDate(Date date) {
        if (date == null) return getString(R.string.date_invalide);
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(
                java.text.DateFormat.MEDIUM,
                java.text.DateFormat.SHORT,
                Locale.getDefault()
        );
        return dateFormat.format(date);
    }

    private void afficherToutesSoumissions(List<Soumission> liste, String role) {
        List<String> affichages = new ArrayList<>();
        for (Soumission s : liste) {
            String item = getString(R.string.travaux_affichage, s.getTravaux().get(0)) + "\n" +
                    getString(R.string.client_affichage, s.getPrenomClient()) + "\n" +
                    getLocalizedDate(s.getDate());
            affichages.add(item);
        }

        ListView listView = findViewById(R.id.listViewAllSoumissions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, affichages);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Soumission soum = liste.get(position);
            Intent intent = new Intent(AllSoumissionsActivity.this, SoumInfosActivity.class);

            intent.putExtra("soumissionId", soum.getId());
            intent.putExtra("prenom", soum.getPrenomClient());
            intent.putExtra("role", role);
            intent.putExtra("clientId", soum.getClientId());
            intent.putExtra("email", soum.getEmail());
            intent.putExtra("adresse", soum.getAdresse());
            intent.putExtra("telephone", soum.getTelephone());
            intent.putExtra("description", soum.getDescription());
            intent.putExtra("travaux", soum.getTravaux().toArray(new String[0]));
            intent.putExtra("date", soum.getDate());

            startActivity(intent);
        });
    }
}
