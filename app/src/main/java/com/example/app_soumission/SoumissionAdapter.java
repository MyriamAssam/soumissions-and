package com.example.app_soumission;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SoumissionAdapter extends ArrayAdapter<Soumission> {
    private final Context context;
    private final List<Soumission> soumissions;

    public SoumissionAdapter(@NonNull Context context, @NonNull List<Soumission> soumissions) {
        super(context, 0, soumissions);
        this.context = context;
        this.soumissions = soumissions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Soumission soum = soumissions.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_soumission, parent, false);
        }

        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textTravaux = convertView.findViewById(R.id.textTravaux);

        Date date = soum.getDate();
        if (date != null) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
            textDate.setText(df.format(date));
        } else {
            textDate.setText(context.getString(R.string.date_invalide));
        }

        List<String> travaux = soum.getTravaux();
        textTravaux.setText(travaux != null && !travaux.isEmpty() ? travaux.get(0) : context.getString(R.string.travaux_inconnus));

        return convertView;
    }
}
