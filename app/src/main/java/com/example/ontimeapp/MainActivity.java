    package com.example.ontimeapp;

    import android.app.Activity;
    import android.content.ContentResolver;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.MotionEvent;
    import android.view.View;
    import android.webkit.MimeTypeMap;
    import android.widget.Button;
    import android.widget.CalendarView;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.RelativeLayout;
    import android.widget.Switch;
    import android.widget.TextView;
    import android.widget.Toast;
    import com.example.ontimeapp.Schedule;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.SwitchCompat;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.ontimeapp.databinding.ActivityMainBinding;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.List;
    import java.util.Locale;



    public class MainActivity extends AppCompatActivity implements RecyclerViewInterface, ScheduleCheckInterface, DownloadViewInterface{

        private StorageReference storageReference;
        private Uri imageUri;
        FirebaseAuth auth;
        Button logout, buttonSchedule, completeSave;
        TextView username;
        FirebaseUser user;
        ImageView fab, addSchedule, scheduleHistory, addImage, addNote, searchNoteImage;
        SwitchCompat switchOnOff;
        CalendarView calendarView;
        String selectedDate, fullName;
        HistoryAdapter historyAdapter;
        EditText searchNoteEditText;
        RecyclerView recyclerViewSchedule, recyclerViewHistory, recyclerViewNote;
        ScheduleAdapter scheduleAdapter;
        NotesAdapter notesAdapter;
        List<Schedule> scheduleList;
        List<Note> notesList;
        List<ScheduleHistory> scheduleHistoryList;
        ConstraintLayout main, logoutPopUp;
        DatabaseReference historyRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            auth = FirebaseAuth.getInstance();
            logout = findViewById(R.id.logout);
            username = findViewById(R.id.username);
            user = auth.getCurrentUser();
            fab = findViewById(R.id.floatingActionButton);
            addSchedule = findViewById(R.id.addSchedule);
            switchOnOff = findViewById(R.id.switchOnOff);
            calendarView = findViewById(R.id.calendarView);
            switchOnOff.setThumbResource(R.drawable.thumb_selector);
            switchOnOff.setTrackResource(android.R.color.transparent);
            recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
            recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
            scheduleList = new ArrayList<>();
            scheduleHistoryList = new ArrayList<>();
            historyAdapter = new HistoryAdapter((Context) this, scheduleHistoryList, (DownloadViewInterface) this);
            scheduleAdapter = new ScheduleAdapter(scheduleList, (Context) this, this, this);
            recyclerViewSchedule.setAdapter(scheduleAdapter);
            buttonSchedule = findViewById(R.id.buttonSchedule);
            scheduleHistory = findViewById(R.id.scheduleHistory);
            recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
            recyclerViewHistory.setAdapter(historyAdapter);
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewNote = findViewById(R.id.recyclerViewNote);
            recyclerViewNote.setLayoutManager(new LinearLayoutManager(this));
            notesList = new ArrayList<>();
            notesAdapter = new NotesAdapter((Context) this, (List<Note>) notesList);
            recyclerViewNote.setAdapter(notesAdapter);

            main = findViewById(R.id.main);
            logoutPopUp = findViewById(R.id.logout_pop_up);

            searchNoteEditText=findViewById(R.id.searchNoteEditText);
            searchNoteImage = findViewById(R.id.searchNoteImage);
            addNote = findViewById(R.id.addNote);
            recyclerViewNote = findViewById(R.id.recyclerViewNote);

            setDefaultState();

            buttonSchedule.setBackgroundResource(R.drawable.schedule_main);
            scheduleHistory.setBackgroundColor(Color.TRANSPARENT);

            buttonSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Button clicked: set its background and clear ImageView's background
                    buttonSchedule.setBackgroundResource(R.drawable.schedule_main);
                    scheduleHistory.setBackgroundColor(Color.TRANSPARENT);
                    setDefaultState();
                }
            });

            scheduleHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ImageView clicked: set its background and clear Button's background
                    scheduleHistory.setBackgroundResource(R.drawable.schedule_main);
                    buttonSchedule.setBackgroundColor(Color.TRANSPARENT);
                    recyclerViewSchedule.setVisibility(View.GONE);
                    addSchedule.setVisibility(View.GONE);
                    recyclerViewHistory.setVisibility(View.VISIBLE);
                }
            });

            switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    setNoteState(); // Show notes UI
                } else {
                    setDefaultState(); // Show default UI
                }
            });

            fetchSchedulesFromDatabase();
            fetchSchedulesHistoryFromDatabase();
            fetchNotesFromDatabase();

            // Fetch your data from Firebase and update the list
            FirebaseDatabase db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
            DatabaseReference reference = db.getReference("Users");
            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference schedulesRef = db.getReference("Users").child(userUid).child("Schedules");

            if (user == null){
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
            else {
                db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
                reference= db.getReference("Users").child(user.getUid()).child("fullName");;
            }

            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fullName = task.getResult().getValue(String.class);
                    if (fullName != null) {
                        username.setText(fullName);
                    } else {
                        username.setText("Full Name not found");
                    }
                } else {
                    username.setText("Error fetching name");
                }
            });

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
            selectedDate = dateFormat.format(calendar.getTime()); // Get current date

            // Set the calendar to the current date if no date is selected
            calendarView.setDate(calendar.getTimeInMillis(), false, true);

            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                // Create a Calendar instance and set it to the selected date
                calendar.set(year, month, dayOfMonth);

                // Format the date as "Mon, 20 Sep 2021"
                selectedDate = dateFormat.format(calendar.getTime());
            });

            addSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V){
                    if (selectedDate != null) {
                        // Pass the selected date to add_schedule activity
                        Intent intent = new Intent(MainActivity.this, add_schedule.class);
                        intent.putExtra("selectedDate", selectedDate);
                        startActivity(intent);}
                }
            });

            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V){
                        // Pass the selected date to add_schedule activity
                        Intent intent = new Intent(MainActivity.this, AddNotes.class);
                        startActivity(intent);
                }
            });



            fab.setOnClickListener(v -> {
                logoutPopUp.setVisibility(View.VISIBLE); // Show the logout popup
            });

            // Set touch listener on the parent layout to hide popup when touched outside
            main.setOnTouchListener((v, event) -> {
                // If touch is outside the popup, hide it
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isViewClicked(logoutPopUp, event)) {
                        logoutPopUp.setVisibility(View.GONE); // Hide the logout popup
                    }
                }
                return true;
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });


        }

        private void fetchSchedulesFromDatabase() {
            // Get the reference to the Firebase database
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
            DatabaseReference schedulesRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Schedules");

            schedulesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scheduleList.clear(); // Clear the list before populating new data

                    // Iterate through the database snapshot to get data and add it to the list
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Schedule schedule = snapshot.getValue(Schedule.class);
                        if (schedule != null) {
                            scheduleList.add(schedule);
                        }
                    }

                    // Notify the adapter about data changes
                    scheduleAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database errors
                    Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchSchedulesHistoryFromDatabase() {
            // Get the reference to the Firebase database for schedule history
            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            historyRef = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users").child(userUid).child("SchedulesHistory");

            historyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    scheduleHistoryList.clear(); // Clear the list before populating new data

                    // Iterate through the database snapshot to get data and add it to the history list
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ScheduleHistory scheduleHistory = dataSnapshot.getValue(ScheduleHistory.class);
                        if (scheduleHistory != null) {
                            scheduleHistoryList.add(scheduleHistory);
                        }
                    }

                    Collections.reverse(scheduleHistoryList);

                    // Notify the history adapter about data changes
                    historyAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to load history.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchNotesFromDatabase() {
            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference notesRef = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users").child(userUid).child("Notes");

            notesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notesList.clear(); // Clear the list before adding new data

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Note note = dataSnapshot.getValue(Note.class);
                        if (note != null) {
                            notesList.add(note);
                        }
                    }

                    Collections.sort(notesList, (note1, note2) -> Boolean.compare(
                            note2.getPinned() != null && note2.getPinned(),
                            note1.getPinned() != null && note1.getPinned()
                    ));

                    // Notify the adapter about the data changes
                    notesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to load notes.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void onHistoryClick(int position) {
            ScheduleHistory selectedItem = scheduleHistoryList.get(position);

            Intent intent = new Intent(this, DownloadLayout.class);
            intent.putExtra("name", selectedItem.getName());
            intent.putExtra("place", selectedItem.getPlace());
            intent.putExtra("description", selectedItem.getDescription());
            intent.putExtra("imageUrl", selectedItem.getImageUrl());
            intent.putExtra("date", selectedItem.getDate());
            intent.putExtra("startTime", selectedItem.getStartTime());
            intent.putExtra("finishTime", selectedItem.getFinishTime());
            startActivity(intent);
        }

        @Override
        public void onItemClick(int position) {
            // Get the selected schedule from the list
            Schedule selectedSchedule = scheduleList.get(position);

            // Create an Intent to start the EditSchedule activity
            Intent intent = new Intent(MainActivity.this, EditSchedule.class);

            // Pass the schedule data to the EditSchedule activity
            intent.putExtra("date", selectedSchedule.getDate());
            intent.putExtra("name", selectedSchedule.getName());
            intent.putExtra("place", selectedSchedule.getPlace());
            intent.putExtra("description", selectedSchedule.getDescription());
            intent.putExtra("startTime", selectedSchedule.getStartTime());
            intent.putExtra("finishTime", selectedSchedule.getFinishTime());
            intent.putExtra("reminderTime", selectedSchedule.getReminderTime());
            intent.putExtra("repeat", selectedSchedule.getRepeat());
            intent.putExtra("scheduleId", selectedSchedule.getScheduleId());

            // Start the EditSchedule activity
            startActivity(intent);
            finish(); // Optionally finish the current activity if you don't want to go back to it
        }

        private void setDefaultState() {
            //Schedule
            calendarView.setVisibility(View.VISIBLE);
            buttonSchedule.setVisibility(View.VISIBLE);
            scheduleHistory.setVisibility(View.VISIBLE);
            recyclerViewSchedule.setVisibility(View.VISIBLE);
            addSchedule.setVisibility(View.VISIBLE);

            //History
            recyclerViewHistory.setVisibility(View.GONE);

            //Notes
            searchNoteEditText.setVisibility(View.GONE);
            searchNoteImage.setVisibility(View.GONE);
            addNote.setVisibility(View.GONE);
            recyclerViewNote.setVisibility(View.GONE);
        }

        private void setNoteState(){
            //Schedule
            calendarView.setVisibility(View.GONE);
            buttonSchedule.setVisibility(View.GONE);
            scheduleHistory.setVisibility(View.GONE);
            recyclerViewSchedule.setVisibility(View.GONE);
            addSchedule.setVisibility(View.GONE);

            //History
            recyclerViewHistory.setVisibility(View.GONE);

            //Notes
            searchNoteEditText.setVisibility(View.VISIBLE);
            searchNoteImage.setVisibility(View.VISIBLE);
            addNote.setVisibility(View.VISIBLE);
            recyclerViewNote.setVisibility(View.VISIBLE);
        }

        @Override
        public void onScheduleChecked(boolean isChecked, int pos) {
            Schedule selectedSchedule = scheduleList.get(pos);

            // Create an Intent to start the EditSchedule activity
            Intent intent = new Intent(MainActivity.this, CompleteSchedule.class);

            // Pass the schedule data to the EditSchedule activity
            intent.putExtra("date", selectedSchedule.getDate());
            intent.putExtra("name", selectedSchedule.getName());
            intent.putExtra("place", selectedSchedule.getPlace());
            intent.putExtra("description", selectedSchedule.getDescription());
            intent.putExtra("startTime", selectedSchedule.getStartTime());
            intent.putExtra("finishTime", selectedSchedule.getFinishTime());
            intent.putExtra("reminderTime", selectedSchedule.getReminderTime());
            intent.putExtra("repeat", selectedSchedule.getRepeat());
            intent.putExtra("scheduleId", selectedSchedule.getScheduleId());

            // Start the EditSchedule activity
            startActivity(intent);
        }

        private boolean isViewClicked(View view, MotionEvent event) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);

            int left = location[0];
            int top = location[1];
            int right = left + view.getWidth();
            int bottom = top + view.getHeight();

            return event.getRawX() >= left && event.getRawX() <= right &&
                    event.getRawY() >= top && event.getRawY() <= bottom;
        }

    }