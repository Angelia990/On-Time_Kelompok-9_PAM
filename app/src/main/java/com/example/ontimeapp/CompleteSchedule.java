package com.example.ontimeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CompleteSchedule extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;

    EditText notesComplete;
    ImageView addImage;
    Button completeSave;
    RelativeLayout completeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.complete_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Initialize UI components
        completeLayout = findViewById(R.id.complete_layout);
        addImage = findViewById(R.id.add_image);
        completeSave = findViewById(R.id.save_complete);
        notesComplete = findViewById(R.id.add_note_complete);

        storageReference = FirebaseStorage.getInstance("gs://tugaskelompokpapb.firebasestorage.app").getReference("images");

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String place = intent.getStringExtra("place");
        String description = intent.getStringExtra("description");
        String startTime = intent.getStringExtra("startTime");
        String finishTime = intent.getStringExtra("finishTime");
        String reminderTime = intent.getStringExtra("reminderTime");
        String repeat = intent.getStringExtra("repeat");
        String scheduleId = intent.getStringExtra("scheduleId");
        String date = intent.getStringExtra("date");

        notesComplete.setText(description);

        notesComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                notesComplete.setHint("");
            } else if (notesComplete.getText().toString().isEmpty()) {
                notesComplete.setHint("Add Notes here");
            }
        });

        addImage.setOnClickListener(v -> openFileChooser());

        completeSave.setOnClickListener(v -> {
            String updatedDescription = notesComplete.getText().toString().trim();
            if (updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please add notes before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            moveScheduleToHistory(scheduleId, name, place, updatedDescription, startTime, finishTime, reminderTime, repeat, date);
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
        }
    }

    private void moveScheduleToHistory(String scheduleId, String name, String place, String description,
                                       String startTime, String finishTime, String reminderTime, String repeat, String date) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference schedulesRef = database.getReference("Users").child(userUid).child("Schedules").child(scheduleId);
        DatabaseReference historyRef = database.getReference("Users").child(userUid).child("SchedulesHistory").child(scheduleId);

        if (imageUri != null) {
            // Get file extension and create a unique file name
            String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(imageUri));
            StorageReference fileRef = storageReference.child(scheduleId + "." + fileExtension);

            // Upload the image to Firebase Storage
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Get the image URL after upload
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Prepare data with image URL
                    Map<String, Object> scheduleData = new HashMap<>();
                    scheduleData.put("name", name);
                    scheduleData.put("place", place);
                    scheduleData.put("description", description); // Update description with notes
                    scheduleData.put("startTime", startTime);
                    scheduleData.put("finishTime", finishTime);
                    scheduleData.put("reminderTime", reminderTime);
                    scheduleData.put("repeat", repeat);
                    scheduleData.put("date", date);
                    scheduleData.put("imageUrl", imageUrl); // Add image URL

                    // Add to history
                    historyRef.setValue(scheduleData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Remove from schedules
                            schedulesRef.removeValue().addOnCompleteListener(removeTask -> {
                                if (removeTask.isSuccessful()) {
                                    finish();
                                }
                            });
                        }
                    });
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve image URL.", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
        } else {
            // Prepare data without image URL
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("name", name);
            scheduleData.put("place", place);
            scheduleData.put("description", description); // Update description with notes
            scheduleData.put("startTime", startTime);
            scheduleData.put("finishTime", finishTime);
            scheduleData.put("reminderTime", reminderTime);
            scheduleData.put("repeat", repeat);
            scheduleData.put("date", date);

            // Add to history
            historyRef.setValue(scheduleData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Remove from schedules
                    schedulesRef.removeValue().addOnCompleteListener(removeTask -> {
                        if (removeTask.isSuccessful()) {
                            finish();
                        }
                    });
                }
            });
        }
    }
}
