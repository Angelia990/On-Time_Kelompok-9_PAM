package com.example.ontimeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private ScheduleCheckInterface scheduleCheckInterface;


    public ScheduleAdapter(List<Schedule> scheduleList, Context context, RecyclerViewInterface recyclerViewInterface, ScheduleCheckInterface scheduleCheckInterface) {
        this.scheduleList = scheduleList;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.scheduleCheckInterface = scheduleCheckInterface;
        this.db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.reference = db.getReference("Users");
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view, recyclerViewInterface, scheduleCheckInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);  // Get the current Schedule object

        // Set the text for each TextView using the corresponding getters
        holder.activityView.setText(schedule.getName());  // For activity name
        String timeRange = schedule.getStartTime() + " - " + schedule.getFinishTime();
        holder.timeView.setText(timeRange);   // Format and show time range
        holder.placeView.setText(schedule.getPlace());  // For place
        holder.notesView.setText(schedule.getDescription());
        String formattedDate = formatDate(schedule.getDate());
        holder.dateView.setText(formattedDate);  // Display only the day (e.g., "13")
    }

    private String formatDate(String dateString) {
        try {
            // Define the input format to match the full date string ("Sun, 13 Nov 2024")
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

            // Parse the input string to a Date object
            Date date = inputFormat.parse(dateString);

            if (date != null) {
                // Print the original and formatted date for debugging
                System.out.println("Original Date: " + dateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd", Locale.getDefault());
                String formattedDate = outputFormat.format(date);  // Format only the day
                System.out.println("Formatted Date: " + formattedDate);  // Debug output
                return formattedDate;
            } else {
                return dateString;  // Return the original string if parsing fails
            }

        } catch (Exception e) {
            e.printStackTrace();
            return dateString;  // Return original string if there is an error
        }
    }


    @Override
    public int getItemCount() {
        return scheduleList.size();
    }


    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView activityView, timeView, placeView, notesView, dateView;
        CheckBox checkBoxDone;

        public ScheduleViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface, ScheduleCheckInterface scheduleCheckInterface) {
            super(itemView);
            activityView = itemView.findViewById(R.id.activityView);
            timeView = itemView.findViewById(R.id.timeView);
            placeView = itemView.findViewById(R.id.placeView);
            notesView = itemView.findViewById(R.id.notesView);
            dateView = itemView.findViewById(R.id.dateView);
            checkBoxDone = itemView.findViewById(R.id.checkBoxDone);

            checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (scheduleCheckInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        scheduleCheckInterface.onScheduleChecked(isChecked, pos);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int pos = getAbsoluteAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}

