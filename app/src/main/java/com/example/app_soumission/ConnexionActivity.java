package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ConnexionActivity extends AppCompatActivity {

    EditText emailEdit, mdpEdit;
    Button btnConnexion, btnClient, btnEmploye;
    String type = "";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase)); // ✅ applique la langue ici
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");
        setLocaleIfUserChose(lang); // ✅ Appliquer la langue AVANT onCreate()

        super.onCreate(savedInstanceState); // toujours après le setLocale
        setContentView(R.layout.activity_connexion);
        emailEdit = findViewById(R.id.editTextText);
        mdpEdit = findViewById(R.id.editTextText2);
        btnConnexion = findViewById(R.id.button6);
        btnClient = findViewById(R.id.button3);
        btnEmploye = findViewById(R.id.button4);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnClient.setOnClickListener(v -> {
            type = "client";
            btnClient.setBackgroundColor(getResources().getColor(R.color.rouge));
            btnEmploye.setBackgroundColor(getResources().getColor(R.color.beige));
        });

        btnEmploye.setOnClickListener(v -> {
            type = "employé";
            btnEmploye.setBackgroundColor(getResources().getColor(R.color.rouge));
            btnClient.setBackgroundColor(getResources().getColor(R.color.beige));
        });

        btnConnexion.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String mdp = mdpEdit.getText().toString();

            if (email.isEmpty() || mdp.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.signInWithEmailAndPassword(email, mdp)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                db.collection("users").document(user.getUid()).get()
                                        .addOnSuccessListener(snapshot -> {
                                            if (snapshot.exists()) {
                                                String role = snapshot.getString("role");

                                                if (!type.equals(role)) {


                                                    Toast.makeText(this, getString(R.string.error_wrong_role), Toast.LENGTH_LONG).show();

                                                    mAuth.signOut();
                                                    return;
                                                }


                                                String prenom = snapshot.getString("prenom");
                                                String emailRecup = snapshot.getString("email");
                                                String adresse = snapshot.getString("adresse");
                                                String telephone = snapshot.getString("telephone");
                                                String specialite = snapshot.getString("specialite");

                                                Toast.makeText(this, getString(R.string.welcome, prenom), Toast.LENGTH_LONG).show();



                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("role", role);
                                                editor.putString("prenom", prenom);
                                                editor.apply();

                                                Intent intent = new Intent(this, SoumissionsActivity.class);
                                                intent.putExtra("userId", user.getUid());
                                                intent.putExtra("prenom", prenom);
                                                intent.putExtra("email", emailRecup);
                                                intent.putExtra("adresse", adresse);
                                                intent.putExtra("telephone", telephone);
                                                intent.putExtra("role", role);

                                                if ("employé".equals(role)) {
                                                    intent.putExtra("specialite", specialite);
                                                }

                                                startActivity(intent);
                                                finish();
                                            }

                                        });
                            }

                        } else {
                            Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();

                        }
                    });
        });

    }
}
