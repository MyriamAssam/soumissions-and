package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class SoumissionsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    SharedPreferences prefs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLocales(new LocaleList(locale));
            getApplicationContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soumissions);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");
        setLocale(lang);

        db = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String role = getIntent().getStringExtra("role");
        String specialite = getIntent().getStringExtra("specialite");
        Button button19 = findViewById(R.id.button19);
        Button buttonCreerSoumission = findViewById(R.id.buttonCreerSoumission);
        Button btn_dec = findViewById(R.id.btn_dec);

        if (role.equals("client")) {
            buttonCreerSoumission.setVisibility(View.VISIBLE);

            db.collection("soumissions")
                    .whereEqualTo("clientId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Soumission> soumissions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            soumissions.add(parseSoumission(doc.getId(), doc.getData()));
                        }
                        afficherSoumissions(soumissions);
                    });
        } else if (role.equals("employé")) {
            buttonCreerSoumission.setVisibility(View.GONE);

            db.collection("soumissions")
                    .whereEqualTo("typeTravaux", specialite)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Soumission> soumissions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            soumissions.add(parseSoumission(doc.getId(), doc.getData()));
                        }
                        afficherSoumissions(soumissions);
                    });
        }

        buttonCreerSoumission.setOnClickListener(v -> {
            Intent intent = new Intent(SoumissionsActivity.this, AjoutSoumissionActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("prenom", getIntent().getStringExtra("prenom"));
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("adresse", getIntent().getStringExtra("adresse"));
            intent.putExtra("telephone", getIntent().getStringExtra("telephone"));
            startActivity(intent);
        });

        button19.setOnClickListener(v ->
                startActivity(new Intent(SoumissionsActivity.this, AllSoumissionsActivity.class)));

        btn_dec.setOnClickListener(v -> {
            Intent intent = new Intent(SoumissionsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void afficherSoumissions(List<Soumission> liste) {
        ListView listView = findViewById(R.id.listViewSoumissions);
        SoumissionAdapter adapter = new SoumissionAdapter(this, liste);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Soumission soum = liste.get(position);
            Intent intent = new Intent(SoumissionsActivity.this, SoumInfosActivity.class);
            intent.putExtra("soumissionId", soum.getId());
            intent.putExtra("prenom", soum.getPrenomClient());
            intent.putExtra("email", soum.getEmail());
            intent.putExtra("adresse", soum.getAdresse());
            intent.putExtra("telephone", soum.getTelephone());
            intent.putExtra("description", soum.getDescription());
            intent.putExtra("travaux", soum.getTravaux().toArray(new String[0]));
            intent.putExtra("date", soum.getDate());
            intent.putExtra("role", getIntent().getStringExtra("role"));
            startActivity(intent);
        });
    }

    private Soumission parseSoumission(String id, Map<String, Object> data) {
        Soumission s = new Soumission();
        s.setId(id);
        s.setPrenomClient((String) data.get("clientPrenom"));
        s.setEmail((String) data.get("clientEmail"));
        s.setAdresse((String) data.get("clientAdresse"));
        s.setTelephone((String) data.get("clientTelephone"));
        s.setDescription((String) data.get("description"));
        s.setTravaux(Collections.singletonList((String) data.get("typeTravaux")));

        Object rawDate = data.get("date");
        if (rawDate instanceof Timestamp) {
            s.setDate(((Timestamp) rawDate).toDate());
        } else {
            s.setDate(null);
        }

        return s;
    }
}
