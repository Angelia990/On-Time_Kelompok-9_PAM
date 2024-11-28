package com.example.ontimeapp;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class EditSchedule extends AppCompatActivity {

    TextView startTimeEditText, finishTimeEditText, reminderTimeEditText, repeatEditText, dateEditTextStart, dateEditTextFinish, editReminderSelect, editStartSelect;
    TimePicker editTimePickerStart, editTimePickerFinish, editTimePickerReminder;
    EditText editActivityName, editActivityPlace, editActivityDesc;
    ConstraintLayout main;
    RelativeLayout editRepeatPopUp;
    RadioGroup editRepeatOptionsGroup;
    ImageView saveButton, editBackImg, deleteImg;
    DatabaseReference userScheduleRef;
    String userId, scheduleId;
    Switch editSwitchFullDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        // Initialize UI components
        editActivityName = findViewById(R.id.editActivityName);
        editActivityPlace = findViewById(R.id.editActivityPlace);
        editActivityDesc = findViewById(R.id.editActivityDesc);
        startTimeEditText = findViewById(R.id.editStart_select_clock);
        finishTimeEditText = findViewById(R.id.editFinish_select_clock);
        reminderTimeEditText = findViewById(R.id.editReminder_select);
        repeatEditText = findViewById(R.id.editRepeat_select);
        dateEditTextStart = findViewById(R.id.editStart_select);
        saveButton = findViewById(R.id.editSaveImg);
        editBackImg = findViewById(R.id.editBackImg);
        dateEditTextFinish = findViewById(R.id.editFinish_select);
        editSwitchFullDay = findViewById(R.id.editSwitchFullDay);
        editRepeatOptionsGroup = findViewById(R.id.edit_repeat_options_group);
        editRepeatPopUp = findViewById(R.id.edit_repeat_popup);
        main = findViewById(R.id.main);
        editTimePickerStart = findViewById(R.id.editTimePickerStart);
        editTimePickerFinish = findViewById(R.id.editTimePickerFinish);
        editTimePickerReminder = findViewById(R.id.editTimePickerReminder);
        editStartSelect = findViewById(R.id.editStart_select);
        deleteImg = findViewById(R.id.deleteImg);


        Intent intent = getIntent();

        // Retrieve the data from the Intent
        String name = intent.getStringExtra("name");
        String place = intent.getStringExtra("place");
        String description = intent.getStringExtra("description");
        String startTime = intent.getStringExtra("startTime");
        String finishTime = intent.getStringExtra("finishTime");
        String reminderTime = intent.getStringExtra("reminderTime");
        String repeat = intent.getStringExtra("repeat");
        String scheduleId = intent.getStringExtra("scheduleId");
        String date = intent.getStringExtra("date");

        // Set the retrieved data to the EditText fields
        editActivityName.setText(name);
        editActivityPlace.setText(place);
        editActivityDesc.setText(description);
        startTimeEditText.setText(startTime);
        finishTimeEditText.setText(finishTime);
        reminderTimeEditText.setText(reminderTime);
        repeatEditText.setText(repeat);
        dateEditTextFinish.setText(date);
        dateEditTextStart.setText(date);

        if ("12:01 AM".equals(startTime) && "11:59 PM".equals(finishTime)) {
            editSwitchFullDay.setChecked(true);
        } else {
            editSwitchFullDay.setChecked(false);
        }

        reminderTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTimePickerReminder.setVisibility(View.VISIBLE);
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the TimePicker visible when the TextView is clicked
                editTimePickerStart.setVisibility(View.VISIBLE);
            }
        });

        finishTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the TimePicker visible when the TextView is clicked
                editTimePickerFinish.setVisibility(View.VISIBLE);
            }
        });

        repeatEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the pop-up visible
                editRepeatPopUp.setVisibility(View.VISIBLE);
            }
        });

        editSwitchFullDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Disable clicking on startSelectClock and finishSelectClock
                startTimeEditText.setClickable(false);
                finishTimeEditText.setClickable(false);

                // Set the time for startSelectClock to 12:01 AM
                editTimePickerStart.setHour(0);
                editTimePickerStart.setMinute(1);
                startTimeEditText.setText(formatTimeWithAMPM(0, 1));

                // Set the time for finishSelectClock to 11:59 PM
                editTimePickerFinish.setHour(23);
                editTimePickerFinish.setMinute(59);
                finishTimeEditText.setText(formatTimeWithAMPM(23, 59));
            } else {
                // Enable clicking on startSelectClock and finishSelectClock
                startTimeEditText.setClickable(true);
                finishTimeEditText.setClickable(true);
            }
        });

        editRepeatOptionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find the selected RadioButton and update the text
                RadioButton selectedOption = findViewById(checkedId);
                String selectedText = selectedOption.getText().toString();
                repeatEditText.setText(selectedText);
            }
        });

        editTimePickerStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Format time with AM/PM
                String time = formatTimeWithAMPM(hourOfDay, minute);
                startTimeEditText.setText(time);
            }
        });

        editTimePickerFinish.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Format time with AM/PM
                String time = formatTimeWithAMPM(hourOfDay, minute);
                finishTimeEditText.setText(time);
            }
        });

        editTimePickerReminder.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String time = formatTimeWithAMPM(hourOfDay, minute);
                editReminderSelect.setText(time);
            }
        });

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if touch happens outside the TimePicker
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // If the touch is outside the TimePicker, hide it
                    if (!isTouchInsideView(editTimePickerStart, event)) {
                        editTimePickerStart.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(editTimePickerFinish, event)) {
                        editTimePickerFinish.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(editTimePickerReminder, event)) {
                        editTimePickerReminder.setVisibility(View.GONE);
                    }
                    if (!isTouchInsideView(editRepeatPopUp, event)) {
                        editRepeatPopUp.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        editBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(EditSchedule.this, MainActivity.class);
                startActivity(intent);
                // Optionally, if you want to finish the current activity (so that the user cannot go back to it by pressing back button)
                finish();
            }
        });

        editActivityName.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                editActivityName.setHint(""); // Clear hint when focused
            } else if (editActivityName.getText().toString().isEmpty()) {
                editActivityName.setHint("Activity"); // Restore hint if no text is entered
            }
        });

        editActivityPlace.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                editActivityPlace.setHint(""); // Clear hint when focused
            } else if (editActivityPlace.getText().toString().isEmpty()) {
                editActivityPlace.setHint("Place"); // Restore hint if no text is entered
            }
        });

        editActivityDesc.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                editActivityDesc.setHint(""); // Clear hint when focused
            } else if (editActivityDesc.getText().toString().isEmpty()) {
                editActivityDesc.setHint("Description"); // Restore hint if no text is entered
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate activityName is not empty
                String name = editActivityName.getText().toString().trim();
                if (name.isEmpty()) {
                    editActivityName.setError("Activity name cannot be empty");
                    return;
                }

                // Collect data to save
                String place = editActivityPlace.getText().toString().trim();
                String desc = editActivityDesc.getText().toString().trim();
                String startTime = startTimeEditText.getText().toString();
                String finishTime = finishTimeEditText.getText().toString();
                String reminderTime = reminderTimeEditText.getText().toString();
                String repeat = repeatEditText.getText().toString();
                String date = editStartSelect.getText().toString();

                // Save to Firebase
                FirebaseDatabase db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference schedulesRef = db.getReference("Users").child(userUid).child("Schedules").child(scheduleId);

                Schedule updatedSchedule = new Schedule(name, place, desc, startTime, finishTime, reminderTime, repeat, date, scheduleId);


                // Save the schedule under a unique ID


                schedulesRef.setValue(updatedSchedule)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Show success message
                                Toast.makeText(EditSchedule.this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();

                                // Optionally, go back to the main activity
                                Intent intent = new Intent(EditSchedule.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Show error message
                                Toast.makeText(EditSchedule.this, "Failed to update schedule", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleId != null && !scheduleId.isEmpty()) {

                    FirebaseDatabase db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference scheduleRef = db.getReference("Users").child(userUid).child("Schedules").child(scheduleId);

                    scheduleRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditSchedule.this, "Schedule deleted successfully", Toast.LENGTH_SHORT).show();

                            // Navigate back to the MainActivity
                            Intent mainIntent = new Intent(EditSchedule.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(EditSchedule.this, "Failed to delete schedule", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditSchedule.this, "Schedule ID is invalid or missing", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
