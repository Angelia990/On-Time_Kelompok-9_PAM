package com.example.ontimeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private final Context context;
    private final List<Note> notesList;

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notesList.get(position);

        // Set the note description
        holder.notesDesc.setText(note.getDesc());

        // Format and set the note date
        String formattedDate = formatDate(note.getDate());
        holder.notesDate.setText(formattedDate);

        // Set the visibility of the pinned icon
        if (Boolean.TRUE.equals(note.getPinned())) {  // Use Boolean.TRUE.equals() to safely check for null
            holder.notesPinned.setVisibility(View.VISIBLE);
        } else {
            holder.notesPinned.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView notesDesc, notesDate;
        ImageView notesPinned;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notesDesc = itemView.findViewById(R.id.notes_desc);
            notesDate = itemView.findViewById(R.id.notes_date);
            notesPinned = itemView.findViewById(R.id.notes_pinned);
        }
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Return the original date if parsing fails
        }
    }
}
