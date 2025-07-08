package com.example.app_soumission;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.*;

import java.util.*;

public class NotesActivity extends AppCompatActivity {

    private EditText editTextNewNote;
    private ListView listView;
    private Button button9;
    private NoteAdapter adapter;
    private List<Note> noteObjects = new ArrayList<>();
    private String role, soumissionId, auteur;
    private String noteIdEnEdition = null;

    private ApiService apiService;
    private String token;

    public static class Note {
        String id, auteur, texte;
        Date date;

        public Note(String id, String auteur, String texte, Date date) {
            this.id = id;
            this.auteur = auteur;
            this.texte = texte;
            this.date = date;
        }

        @Override
        public String toString() {
            return auteur + " (" + date + "):\n" + texte;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        editTextNewNote = findViewById(R.id.editTextNewNote);
        listView = findViewById(R.id.listView);
        button9 = findViewById(R.id.button9);
        Button btnRetour = findViewById(R.id.btn_retour);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = prefs.getString("token", "");
        auteur = prefs.getString("prenom", "Inconnu");
        role = prefs.getString("role", "");

        soumissionId = getIntent().getStringExtra("soumissionId");

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        adapter = new NoteAdapter(this, new ArrayList<>(), new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onDelete(int position) {
                String noteId = noteObjects.get(position).id;
                apiService.deleteNote(soumissionId, noteId, role, "Bearer " + token)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(NotesActivity.this, getString(R.string.note_sauvegardee), Toast.LENGTH_SHORT).show();
                                    resetForm();
                                    chargerNotes();
                                } else {
                                    Toast.makeText(NotesActivity.this, "Erreur suppression note: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Erreur suppression", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onEdit(int position) {
                Note note = noteObjects.get(position);
                editTextNewNote.setText(note.texte);
                noteIdEnEdition = note.id;
                button9.setText(R.string.modifier);
            }
        });

        listView.setAdapter(adapter);

        button9.setOnClickListener(v -> validerNote());
        btnRetour.setOnClickListener(v -> finish());

        chargerNotes();
    }

    private void validerNote() {
        String texteNote = editTextNewNote.getText().toString().trim();
        if (texteNote.isEmpty()) {
            Toast.makeText(this, getString(R.string.note_vide), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID().toString()); // facultatif mais recommandé
        data.put("texte", texteNote); // 🔥 correction
        data.put("auteur", auteur);
        data.put("role", role);
        data.put("timestamp", new Date());



        if (noteIdEnEdition != null) {
            apiService.updateNote(soumissionId, noteIdEnEdition, data, "Bearer " + token)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(NotesActivity.this, getString(R.string.note_modifiee), Toast.LENGTH_SHORT).show();
                            resetForm();
                            chargerNotes();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(NotesActivity.this, "Erreur modification", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            apiService.addNote(soumissionId, data, "Bearer " + token)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            resetForm();
                            chargerNotes();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(NotesActivity.this, "Erreur ajout", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void chargerNotes() {
        apiService.getNotes(soumissionId, role, "Bearer " + token)
                .enqueue(new Callback<NotesListResponse>() {
                    @Override
                    public void onResponse(Call<NotesListResponse> call, Response<NotesListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            noteObjects.clear();
                            List<NoteResponse> notes = response.body().getNotes();

                            for (NoteResponse n : notes) {
                                if (role.equals(n.getRole())) {
                                    // plus besoin de filtrer par role, tu as déjà demandé notesClients ou notesEmployes selon le rôle
                                    NotesActivity.Note note = new NotesActivity.Note(
                                            n.getId(), n.getAuteur(), n.getNote(), n.getTimestamp()
                                    );
                                    noteObjects.add(note);

                                }
                            }

                            adapter.clear();
                            adapter.addAll(noteObjects);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<NotesListResponse> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "Erreur chargement notes", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void resetForm() {
        editTextNewNote.setText("");
        noteIdEnEdition = null;
        button9.setText(R.string.ajouter);
    }
}
