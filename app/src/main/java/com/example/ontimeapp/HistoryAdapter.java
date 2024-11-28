package com.example.ontimeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ScheduleHistory> scheduleHistoryList;
    private Context context;
    private final DownloadViewInterface downloadViewInterface;

    public HistoryAdapter(Context context, List<ScheduleHistory> scheduleHistoryList, DownloadViewInterface downloadViewInterface) {
        this.context = context;
        this.scheduleHistoryList = scheduleHistoryList;
        this.downloadViewInterface = downloadViewInterface;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view, downloadViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ScheduleHistory scheduleHistory = scheduleHistoryList.get(position);

        // Set the name
        holder.historyActivityView.setText(scheduleHistory.getName());

        // Set the time (formatted)
        String timeRange = scheduleHistory.getStartTime() + " - " + scheduleHistory.getFinishTime();
        holder.historyTimeView.setText(timeRange);

        // Set the place
        holder.historyPlaceView.setText(scheduleHistory.getPlace());

        // Set the description
        holder.historyNotesView.setText(scheduleHistory.getDescription());

        String formattedDate = formatDate(scheduleHistory.getDate());
        holder.historyDateView.setText(formattedDate);

        // Load the image (if present)
        if (scheduleHistory.getImageUrl() != null && !scheduleHistory.getImageUrl().isEmpty()) {
            Glide.with(context).load(scheduleHistory.getImageUrl()).into(holder.histoyImageView);
        } else {
            // Handle default or error case if there's no image
            holder.histoyImageView.setImageResource(R.drawable.add_image);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleHistoryList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView historyActivityView, historyTimeView, historyPlaceView, historyNotesView, historyDateView;
        ImageView histoyImageView;

        public HistoryViewHolder(@NonNull View itemView, DownloadViewInterface downloadViewInterface) {
            super(itemView);

            historyActivityView = itemView.findViewById(R.id.historyActivityView);
            historyTimeView = itemView.findViewById(R.id.historyTimeView);
            historyPlaceView = itemView.findViewById(R.id.HistoryPlaceView);
            historyNotesView = itemView.findViewById(R.id.historyNotesView);
            histoyImageView = itemView.findViewById(R.id.histoyImageView);
            historyDateView = itemView.findViewById(R.id.historyDateView);

            itemView.setOnClickListener(v -> {
                if (downloadViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        downloadViewInterface.onHistoryClick(pos); // Trigger callback
                    }
                }
            });
        }
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


}

