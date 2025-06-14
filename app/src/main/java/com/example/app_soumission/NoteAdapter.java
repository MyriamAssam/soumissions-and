package com.example.app_soumission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<String> {
    private final List<String> notes;
    private final OnNoteActionListener listener;

    public interface OnNoteActionListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    public NoteAdapter(Context context, List<String> notes, OnNoteActionListener listener) {
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

        tvNote.setText(notes.get(position));

        btnEdit.setOnClickListener(v -> listener.onEdit(position));
        btnDelete.setOnClickListener(v -> listener.onDelete(position));

        return convertView;
    }
}


