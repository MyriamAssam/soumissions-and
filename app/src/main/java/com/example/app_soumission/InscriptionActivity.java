
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.app_soumission.RegisterResponse;
import com.example.app_soumission.User;


public class InscriptionActivity extends AppCompatActivity {

    private Button button7, button8, buttonInscription;
    private EditText editTextText3, editTextText4, editTextText5, editTextText6, editTextText7;
    private Spinner spinner;
    String type = "";
    final boolean[] isEmploye = {false};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String lang = prefs.getString("lang", "fr");
        setLocaleIfUserChose(lang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        editTextText3 = findViewById(R.id.editTextText3); // Email
        editTextText4 = findViewById(R.id.editTextText4); // Mot de passe
        editTextText5 = findViewById(R.id.editTextText5); // Adresse
        editTextText6 = findViewById(R.id.editTextText6);
        editTextText6.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                String input = s.toString();
                if (input.equals(current)) return;

                isFormatting = true;

                // Supprimer les caractères non numériques
                String digits = input.replaceAll("\\D", "");

                // Appliquer le format
                StringBuilder formatted = new StringBuilder();
                int len = digits.length();
                for (int i = 0; i < len && i < 10; i++) {
                    if (i == 3 || i == 6) {
                        formatted.append('-');
                    }
                    formatted.append(digits.charAt(i));
                }

                current = formatted.toString();
                editTextText6.setText(current);
                editTextText6.setSelection(current.length());
                isFormatting = false;
            }
        });

// Téléphone
        editTextText7 = findViewById(R.id.editTextText7); // Prénom
        spinner = findViewById(R.id.spinner);
        button7 = findViewById(R.id.button7); // Employé
        button8 = findViewById(R.id.button8); // Client
        buttonInscription = findViewById(R.id.buttonInscription);

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
        EditText mdpEdit = findViewById(R.id.editTextText4);
        ImageView ivShowHidePassword = findViewById(R.id.ivShowHidePassword);

        ivShowHidePassword.setOnClickListener(v -> {
            if (mdpEdit.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                mdpEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowHidePassword.setImageResource(R.drawable.eye_opened);
            } else {
                mdpEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowHidePassword.setImageResource(R.drawable.eye_closed);
            }
            mdpEdit.setSelection(mdpEdit.getText().length()); // 🔥 pour garder le curseur à la fin
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
            String specialite = isEmploye[0] ? spinner.getSelectedItem().toString() : null;

            if (prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!telephone.matches("\\d{3}-\\d{3}-\\d{4}"))
            {
                Toast.makeText(getApplicationContext(), getString(R.string.error_phone_format), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isPasswordValid(motDePasse)) {
                Toast.makeText(getApplicationContext(), getString(R.string.password_rule), Toast.LENGTH_LONG).show();
                return;
            }
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);

            RegisterRequest registerRequest = new RegisterRequest(prenom, email, motDePasse, adresse, telephone, type, specialite);

            apiService.register(registerRequest).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse userResponse = response.body();

                        SharedPreferences.Editor editor = prefs.edit();
                        User user = userResponse.getUser();
                        editor.putString("userId", user.getId());
                        editor.putString("prenom", user.getPrenom());
                        editor.putString("email", user.getEmail());
                        editor.putString("adresse", user.getAdresse());
                        editor.putString("telephone", user.getTelephone());
                        editor.putString("role", user.getRole());
                        editor.putString("specialite", user.getSpecialite());
                        editor.putString("token", userResponse.getToken());

                        editor.apply();

                        Intent intent;
                        if ("client".equals(type)) {
                            intent = new Intent(InscriptionActivity.this, AjoutSoumissionActivity.class);
                        } else {
                            intent = new Intent(InscriptionActivity.this, SoumissionsActivity.class);
                            intent.putExtra("specialite", userResponse.getSpecialite());
                        }

                        intent.putExtra("userId", userResponse.getUserId());
                        intent.putExtra("prenom", userResponse.getPrenom());
                        intent.putExtra("email", userResponse.getEmail());
                        intent.putExtra("adresse", userResponse.getAdresse());
                        intent.putExtra("telephone", userResponse.getTelephone());
                        intent.putExtra("role", userResponse.getRole());

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(InscriptionActivity.this, "Échec de l'inscription : " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(InscriptionActivity.this, "Erreur serveur : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        });
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&/\\\\(){}#^+=._-])[A-Za-z\\d@$!%*?&/\\\\(){}#^+=._-]{8,}$");
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