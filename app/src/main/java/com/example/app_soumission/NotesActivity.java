package com.example.app_soumission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class NotesActivity extends AppCompatActivity {

    private EditText editTextNewNote;
    private ListView listView;
    private Button btnAjouter;
    private NoteAdapter adapter;
    private List<Note> noteObjects = new ArrayList<>();
    private String role, soumissionId, auteur;
    private String noteIdEnEdition = null;
    private String soumissionClientId;

    FirebaseFirestore db;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = FirebaseFirestore.getInstance();

        editTextNewNote = findViewById(R.id.editTextNewNote);
        listView = findViewById(R.id.listView);
        btnAjouter = findViewById(R.id.button9);
        Button btnRetour = findViewById(R.id.btn_retour);

        Intent intent = getIntent();
        role = intent.getStringExtra("role");
        soumissionId = intent.getStringExtra("soumissionId");
        auteur = intent.getStringExtra("prenom");
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (auteur == null) auteur = prefs.getString("prenom", "Inconnu");
        if (role == null) role = prefs.getString("role", "");

        adapter = new NoteAdapter(this, new ArrayList<>(), new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onDelete(int position) {
                String noteId = noteObjects.get(position).id;
                db.collection("soumissions").document(soumissionId)
                        .collection("notes").document(noteId)
                        .delete()
                        .addOnSuccessListener(unused -> chargerNotes(soumissionClientId))
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Erreur suppression", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onEdit(int position) {
                Note note = noteObjects.get(position);
                editTextNewNote.setText(note.texte);
                noteIdEnEdition = note.id;
                btnAjouter.setText("Modifier");
            }
        });

        listView.setAdapter(adapter);


        soumissionClientId = getIntent().getStringExtra("clientId");
        if (soumissionClientId != null) {
            chargerNotes(soumissionClientId);
        } else {
            db.collection("soumissions").document(soumissionId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            soumissionClientId = documentSnapshot.getString("clientId");
                            chargerNotes(soumissionClientId);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur chargement clientId", Toast.LENGTH_SHORT).show());
        } // 👈 cette accolade FERMANTE manquait

        btnAjouter.setOnClickListener(v -> validerNote());
        btnRetour.setOnClickListener(v -> finish());

    }

    private void validerNote() {
        String texteNote = editTextNewNote.getText().toString().trim();
        if (texteNote.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une note", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String auteur = prefs.getString("prenom", "Inconnu");

        if (noteIdEnEdition != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("note", texteNote);
            updates.put("timestamp", Timestamp.now());
            updates.put("auteurId", FirebaseAuth.getInstance().getUid());

            db.collection("soumissions").document(soumissionId)
                    .collection("notes").document(noteIdEnEdition)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Note modifiée", Toast.LENGTH_SHORT).show();
                        resetForm();
                        chargerNotes(soumissionClientId);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur modification", Toast.LENGTH_SHORT).show());

        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("note", texteNote);
            body.put("role", role);
            body.put("auteur", auteur); // ✅ Ici on utilise le prénom 100% fiable
            body.put("timestamp", Timestamp.now());
            body.put("auteurId", FirebaseAuth.getInstance().getUid());

            db.collection("soumissions").document(soumissionId)
                    .collection("notes")
                    .add(body)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Note ajoutée", Toast.LENGTH_SHORT).show();
                        resetForm();
                        chargerNotes(soumissionClientId);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur ajout", Toast.LENGTH_SHORT).show());
        }
    }


    private void resetForm() {
        editTextNewNote.setText("");
        noteIdEnEdition = null;
        btnAjouter.setText("Ajouter");
    }

    private void chargerNotes(String clientId) {
        db.collection("soumissions")
                .document(soumissionId)
                .collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noteObjects.clear();
                    List<String> displayNotes = new ArrayList<>();
                    String currentUserId = FirebaseAuth.getInstance().getUid();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String noteRole = doc.getString("role");
                            String auteurId = doc.getString("auteurId");
                            String id = doc.getId();
                            String auteur = doc.getString("auteur");
                            String texte = doc.getString("note");
                            Timestamp ts = doc.getTimestamp("timestamp");
                            Date date = (ts != null) ? ts.toDate() : new Date();

                            if (noteRole == null || auteur == null || texte == null || auteurId == null) continue;

                            boolean afficher = false;

                            if (role.equals("client") && noteRole.equals("client")) {
                                afficher = true;
                            } else if (role.equals("employé") && noteRole.equals("employé")) {
                                afficher = true;
                            }


                            if (afficher) {
                                Note note = new Note(id, auteur, texte, date);
                                noteObjects.add(note);
                                displayNotes.add(note.toString());
                            }
                        } catch (Exception e) {
                            Log.e("NotesActivity", "Erreur parsing note : " + e.getMessage());
                        }
                    }

                    adapter.clear();
                    adapter.addAll(displayNotes);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Erreur chargement notes", Toast.LENGTH_SHORT).show());
    }
}
