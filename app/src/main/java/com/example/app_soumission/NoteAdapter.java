package com.example.app_soumission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends ArrayAdapter<NotesActivity.Note> {

    private final List<NotesActivity.Note> notes;
    private final OnNoteActionListener listener;

    public interface OnNoteActionListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    public NoteAdapter(Context context, List<NotesActivity.Note> notes, OnNoteActionListener listener) {
        super(context, 0, notes);
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);

        TextView tvNote = convertView.findViewById(R.id.tv_note);
        Button btnEdit = convertView.findViewById(R.id.btn_edit);
        Button btnDelete = convertView.findViewById(R.id.btn_delete);

        NotesActivity.Note note = notes.get(position);

        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(note.date);
        String fullText = note.auteur + " (" + dateFormatted + "):\n" + note.texte;

        tvNote.setText(fullText);

        btnEdit.setOnClickListener(v -> listener.onEdit(position));
        btnDelete.setOnClickListener(v -> listener.onDelete(position));

        return convertView;
    }

    // Optionnel : méthode pour rafraîchir les données si nécessaire
    public void updateNotes(List<NotesActivity.Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }
}
