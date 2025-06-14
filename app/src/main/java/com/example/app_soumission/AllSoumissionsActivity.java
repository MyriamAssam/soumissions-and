package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.*;
import java.util.*;

public class AllSoumissionsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Button buttonMesSoumissions, buttonDeconnexion;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_soumissions);

        db = FirebaseFirestore.getInstance();
        buttonMesSoumissions = findViewById(R.id.button14);
        buttonDeconnexion = findViewById(R.id.button17);

        db.collection("soumissions")
                .whereEqualTo("lectureGlobale", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Soumission> liste = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Soumission s = new Soumission();
                        s.setId(doc.getId());
                        s.setTravaux(Collections.singletonList(doc.getString("typeTravaux")));
                        s.setPrenomClient(doc.getString("clientPrenom"));
                        s.setEmail(doc.getString("clientEmail"));
                        s.setAdresse(doc.getString("clientAdresse"));
                        s.setTelephone(doc.getString("clientTelephone"));
                        s.setDescription(doc.getString("description"));
                        s.setDate(doc.getTimestamp("date") != null ? doc.getTimestamp("date").toDate() : null);
                        s.setClientId(doc.getString("clientId"));
                        liste.add(s);
                    }
                    afficherToutesSoumissions(liste);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, getString(R.string.erreur) + " " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

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

    private void afficherToutesSoumissions(List<Soumission> liste) {
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
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

            intent.putExtra("soumissionId", soum.getId());
            intent.putExtra("prenom", soum.getPrenomClient());
            intent.putExtra("role", prefs.getString("role", ""));
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
