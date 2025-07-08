package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    EditText emailEdit, mdpEdit;
    Button btnConnexion, btnClient, btnEmploye;
    String type = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
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
        setLocaleIfUserChose(lang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        emailEdit = findViewById(R.id.editTextText);
        mdpEdit = findViewById(R.id.editTextText2);
        btnConnexion = findViewById(R.id.button6);
        btnClient = findViewById(R.id.button3);
        btnEmploye = findViewById(R.id.button4);

        btnClient.setOnClickListener(v -> {
            type = "client";
            btnClient.setBackgroundColor(getResources().getColor(R.color.rouge));
            btnEmploye.setBackgroundColor(getResources().getColor(R.color.beige));
        });
        EditText mdpEdit = findViewById(R.id.editTextText2);
        ImageView ivShowHidePassword = findViewById(R.id.ivShowHidePassword);

        ivShowHidePassword.setOnClickListener(v -> {
            if (mdpEdit.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                mdpEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowHidePassword.setImageResource(R.drawable.eye_opened2);
            } else {
                mdpEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowHidePassword.setImageResource(R.drawable.eye_closed2);
            }
            mdpEdit.setSelection(mdpEdit.getText().length()); // 🔥 pour garder le curseur à la fin
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

            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, mdp, type);

            apiService.login(request).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // 👉 AJOUTE CETTE LIGNE ICI :
                        UserResponse userResponse = response.body();

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", userResponse.getToken());
                        editor.putString("prenom", userResponse.getPrenom());
                        editor.putString("role", userResponse.getRole());
                        editor.putString("adresse", userResponse.getAdresse());
                        editor.putString("email", userResponse.getEmail());
                        editor.putString("userId", userResponse.getUserId());
                        editor.putString("telephone", userResponse.getTelephone());
                        editor.putString("specialite", userResponse.getSpecialite());
                        editor.apply();

                        Toast.makeText(ConnexionActivity.this, getString(R.string.welcome, userResponse.getPrenom()), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ConnexionActivity.this, SoumissionsActivity.class);
                        intent.putExtra("userId", userResponse.getUserId());
                        intent.putExtra("prenom", userResponse.getPrenom());
                        intent.putExtra("email", userResponse.getEmail());
                        intent.putExtra("adresse", userResponse.getAdresse());
                        intent.putExtra("telephone", userResponse.getTelephone());
                        intent.putExtra("role", userResponse.getRole());
                        intent.putExtra("specialite", userResponse.getSpecialite());

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConnexionActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(ConnexionActivity.this, "Erreur serveur : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
