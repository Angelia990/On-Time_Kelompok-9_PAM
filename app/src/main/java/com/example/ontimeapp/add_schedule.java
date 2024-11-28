package com.example.ontimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class add_schedule extends AppCompatActivity {
    TextView startSelect, finishSelect, startSelectClock, finishSelectClock, reminderSelect, repeatSelect;
    ImageView backImg, saveImg, deleteImg;
    EditText activityName, activityPlace, activityDesc;
    TimePicker timePickerStart, timePickerFinish, timePickerReminder;
    ConstraintLayout main;
    RelativeLayout repeatPopUp;
    RadioGroup repeatOptionsGroup;
    Switch switchFullDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schedule);

        backImg = findViewById(R.id.backImg);
        startSelect = findViewById(R.id.start_select);
        finishSelect = findViewById(R.id.finish_select);
        activityDesc = findViewById(R.id.activityDesc);
        activityName = findViewById(R.id.activityName);
        activityPlace = findViewById(R.id.activityPlace);
        startSelectClock = findViewById(R.id.start_select_clock);
        timePickerStart = findViewById(R.id.timePickerStart);
        reminderSelect = findViewById(R.id.reminder_select);
        finishSelectClock = findViewById(R.id.finish_select_clock);
        timePickerFinish = findViewById(R.id.timePickerFinish);
        timePickerReminder = findViewById(R.id.timePickerReminder);
        repeatSelect = findViewById(R.id.repeat_select);
        repeatOptionsGroup = findViewById(R.id.repeat_options_group);
        repeatPopUp = findViewById(R.id.repeat_popup);
        switchFullDay = findViewById(R.id.switchFullDay);
        saveImg = findViewById(R.id.saveImg);
        main = findViewById(R.id.main);

        // Enable edge-to-edge insets for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reminderSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerReminder.setVisibility(View.VISIBLE);
            }
        });

        startSelectClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the TimePicker visible when the TextView is clicked
                timePickerStart.setVisibility(View.VISIBLE);
            }
        });

        finishSelectClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the TimePicker visible when the TextView is clicked
                timePickerFinish.setVisibility(View.VISIBLE);
            }
        });

        repeatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the pop-up visible
                repeatPopUp.setVisibility(View.VISIBLE);
            }
        });

        switchFullDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Disable clicking on startSelectClock and finishSelectClock
                startSelectClock.setClickable(false);
                finishSelectClock.setClickable(false);

                // Set the time for startSelectClock to 12:01 AM
                timePickerStart.setHour(0);
                timePickerStart.setMinute(1);
                startSelectClock.setText(formatTimeWithAMPM(0, 1));

                // Set the time for finishSelectClock to 11:59 PM
                timePickerFinish.setHour(23);
                timePickerFinish.setMinute(59);
                finishSelectClock.setText(formatTimeWithAMPM(23, 59));
            } else {
                // Enable clicking on startSelectClock and finishSelectClock
                startSelectClock.setClickable(true);
                finishSelectClock.setClickable(true);
            }
        });

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if touch happens outside the TimePicker
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // If the touch is outside the TimePicker, hide it
                    if (!isTouchInsideView(timePickerStart, event)) {
                        timePickerStart.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(timePickerFinish, event)) {
                        timePickerFinish.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(timePickerReminder, event)) {
                        timePickerReminder.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(repeatPopUp, event)) {
                        repeatPopUp.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        repeatOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find the selected RadioButton and update the text
                RadioButton selectedOption = findViewById(checkedId);
                    String selectedText = selectedOption.getText().toString();
                    repeatSelect.setText(selectedText);
            }
        });

        // Set an OnTimeChangedListener for the TimePicker
        timePickerStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Format time with AM/PM
                String time = formatTimeWithAMPM(hourOfDay, minute);
                startSelectClock.setText(time);
            }
        });

        timePickerFinish.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Format time with AM/PM
                String time = formatTimeWithAMPM(hourOfDay, minute);
                finishSelectClock.setText(time);
            }
        });

        timePickerReminder.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String time = formatTimeWithAMPM(hourOfDay, minute);
                reminderSelect.setText(time);
            }
        });
        

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(add_schedule.this, MainActivity.class);
                startActivity(intent);
                // Optionally, if you want to finish the current activity (so that the user cannot go back to it by pressing back button)
                finish();
            }
        });

        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate activityName is not empty
                String name = activityName.getText().toString().trim();
                if (name.isEmpty()) {
                    activityName.setError("Activity name cannot be empty");
                    return;
                }

                // Collect data to save
                String place = activityPlace.getText().toString().trim();
                String desc = activityDesc.getText().toString().trim();
                String startTime = startSelectClock.getText().toString();
                String finishTime = finishSelectClock.getText().toString();
                String reminderTime = reminderSelect.getText().toString();
                String repeat = repeatSelect.getText().toString();
                String date = startSelect.getText().toString();

                // Save to Firebase
                FirebaseDatabase db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference reference = db.getReference("Users");
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference schedulesRef = db.getReference("Users").child(userUid).child("Schedules");
                String scheduleId = schedulesRef.push().getKey();
                Schedule schedule = new Schedule(name, place, desc, startTime, finishTime, reminderTime, repeat, date, scheduleId);

                // Save the schedule under a unique ID


                schedulesRef.child(scheduleId).setValue(schedule)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Show success message
                                Toast.makeText(add_schedule.this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();

                                // Optionally, go back to the main activity
                                Intent intent = new Intent(add_schedule.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Show error message
                                Toast.makeText(add_schedule.this, "Failed to save schedule", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        activityName.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                activityName.setHint(""); // Clear hint when focused
            } else if (activityName.getText().toString().isEmpty()) {
                activityName.setHint("Activity"); // Restore hint if no text is entered
            }
        });

        activityPlace.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                activityPlace.setHint(""); // Clear hint when focused
            } else if (activityPlace.getText().toString().isEmpty()) {
                activityPlace.setHint("Place"); // Restore hint if no text is entered
            }
        });

        activityDesc.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                activityDesc.setHint(""); // Clear hint when focused
            } else if (activityDesc.getText().toString().isEmpty()) {
                activityDesc.setHint("Description"); // Restore hint if no text is entered
            }
        });

        // Retrieve the selected date from the Intent
        String selectedDate = getIntent().getStringExtra("selectedDate");

        // Display the selected date in a TextView with the ID "start_select"
        if (selectedDate != null) {
            startSelect.setText(selectedDate);
            finishSelect.setText(selectedDate);
        }
    }

    private String formatTimeWithAMPM(int hourOfDay, int minute) {
        // Convert 24-hour format to 12-hour format with AM/PM
        String period = (hourOfDay < 12) ? "AM" : "PM";
        int hour = hourOfDay % 12;
        if (hour == 0) hour = 12;  // Adjust for 12:00 PM (noon) or 12:00 AM (midnight)

        return String.format("%02d:%02d %s", hour, minute, period);
    }

    private boolean isTouchInsideView(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view.getWidth();
        int height = view.getHeight();

        return event.getRawX() >= x && event.getRawX() <= (x + width) &&
                event.getRawY() >= y && event.getRawY() <= (y + height);
    }
}
