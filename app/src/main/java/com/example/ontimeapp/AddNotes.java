package com.example.ontimeapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddNotes extends AppCompatActivity {

    private String userUid;
    private boolean isPinned = false;
    ImageView noteBackImg, pinThisNote, saveNoteButton;
    EditText addNoteTitle, addNoteDesc;
    Date noteDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_notes);

        noteBackImg = findViewById(R.id.noteBackImg);
        pinThisNote = findViewById(R.id.pinThisNotes);
        addNoteTitle = findViewById(R.id.addNoteTitle);
        addNoteDesc = findViewById(R.id.addNoteDesc);
        saveNoteButton = findViewById(R.id.saveNotes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pinThisNote.setOnClickListener(v -> togglePinState());
        // Set onClickListener to save the note
        saveNoteButton.setOnClickListener(v -> saveNoteToFirebase());

        addNoteTitle.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                addNoteTitle.setHint(""); // Clear hint when focused
            } else if (addNoteTitle.getText().toString().isEmpty()) {
                addNoteTitle.setHint("Title"); // Restore hint if no text is entered
            }
        });

        addNoteDesc.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                addNoteDesc.setHint(""); // Clear hint when focused
            } else if (addNoteDesc.getText().toString().isEmpty()) {
                addNoteDesc.setHint("Your description here"); // Restore hint if no text is entered
            }
        });

        noteBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNotes.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void togglePinState() {
        isPinned = !isPinned; // Toggle the state
        if (isPinned) {
            pinThisNote.setColorFilter(Color.DKGRAY); // Set dark blue (use appropriate color code)
            Toast.makeText(this, "Pinned", Toast.LENGTH_SHORT).show();
        } else {
            pinThisNote.setColorFilter(null); // Reset to default
            Toast.makeText(this, "Unpinned", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNoteToFirebase() {
        // Get input data
        String name = addNoteTitle.getText().toString().trim();
        String desc = addNoteDesc.getText().toString().trim();

        // Get the current date
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Validate inputs
        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to the database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference notesRef = db.getReference("Users").child(userUid).child("Notes");

        // Generate a unique key for the note
        String noteId = notesRef.push().getKey();

        // Create a note object
        Note note = new Note(noteId, name, desc, date, isPinned);

        // Save the note to Firebase
        notesRef.child(noteId).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddNotes.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNotes.this, "Failed to add note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}