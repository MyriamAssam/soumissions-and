package com.example.app_soumission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InscriptionActivity extends AppCompatActivity {

    private Button button7, button8, buttonInscription;
    private EditText editTextText3, editTextText4, editTextText5, editTextText6, editTextText7;
    private Spinner spinner;
    String type = "";
    final boolean[] isEmploye = {false};

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Appliquer la langue enregistrée
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");
        setLocaleIfUserChose(lang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        editTextText3 = findViewById(R.id.editTextText3);
        editTextText4 = findViewById(R.id.editTextText4);
        editTextText5 = findViewById(R.id.editTextText5);
        editTextText6 = findViewById(R.id.editTextText6);
        editTextText7 = findViewById(R.id.editTextText7);
        spinner = findViewById(R.id.spinner);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        buttonInscription = findViewById(R.id.buttonInscription);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        spinner.setVisibility(View.GONE);

        String[] travaux = getResources().getStringArray(R.array.travaux_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, travaux);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        button7.setOnClickListener(v -> {
            isEmploye[0] = true;
            spinner.setVisibility(View.VISIBLE);
            spinner.setAdapter(adapter);
            type = "employé";
            button8.setBackgroundColor(getResources().getColor(R.color.bleu));
            button7.setBackgroundColor(Color.parseColor("#62a4e6"));
        });

        button8.setOnClickListener(v -> {
            isEmploye[0] = false;
            spinner.setVisibility(View.GONE);
            type = "client";
            button7.setBackgroundColor(getResources().getColor(R.color.bleu));
            button8.setBackgroundColor(Color.parseColor("#62a4e6"));
        });

        buttonInscription.setOnClickListener(v -> {
            String prenom = editTextText7.getText().toString();
            String email = editTextText3.getText().toString();
            String motDePasse = editTextText4.getText().toString();
            String adresse = editTextText5.getText().toString();
            String telephone = editTextText6.getText().toString();

            if (prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!telephone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_phone_format), Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, motDePasse)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("prenom", prenom);
                                userMap.put("email", email);
                                userMap.put("adresse", adresse);
                                userMap.put("telephone", telephone);
                                userMap.put("role", type);
                                if (isEmploye[0]) {
                                    String specialite = spinner.getSelectedItem().toString();
                                    userMap.put("specialite", specialite);
                                }

                                db.collection("users").document(user.getUid()).set(userMap)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(getApplicationContext(), getString(R.string.success_signup), Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("role", type);
                                            editor.putString("prenom", prenom);
                                            editor.apply();

                                            Intent intent;
                                            if ("client".equals(type)) {
                                                intent = new Intent(this, AjoutSoumissionActivity.class);
                                            } else {
                                                intent = new Intent(this, SoumissionsActivity.class);
                                                intent.putExtra("specialite", userMap.get("specialite").toString());
                                            }

                                            intent.putExtra("userId", user.getUid());
                                            intent.putExtra("prenom", prenom);
                                            intent.putExtra("email", email);
                                            intent.putExtra("adresse", adresse);
                                            intent.putExtra("telephone", telephone);
                                            intent.putExtra("role", type);

                                            startActivity(intent);
                                            finish();
                                        });

                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.error_signup, task.getException().getMessage()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setLocaleIfUserChose(String langCode) {
        if (langCode == null || langCode.isEmpty()) return;

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
}

