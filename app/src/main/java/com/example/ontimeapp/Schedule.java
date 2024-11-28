package com.example.ontimeapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Schedule implements Parcelable {
    String name, place, description, startTime, finishTime, reminderTime, repeat, date, scheduleId;

    public Schedule() {
        // Default constructor required for calls to DataSnapshot.getValue(Schedule.class)
    }

    public Schedule(String name, String place, String description, String startTime, String finishTime, String reminderTime, String repeat, String date, String scheduleId) {
        this.name = name;
        this.place = place;
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.reminderTime = reminderTime;
        this.repeat = repeat;
        this.date = date;
        this.scheduleId = scheduleId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    protected Schedule(Parcel in) {
        name = in.readString();
        place = in.readString();
        description = in.readString();
        startTime = in.readString();
        finishTime = in.readString();
        reminderTime = in.readString();
        repeat = in.readString();
        date = in.readString();
        scheduleId = in.readString();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(place);
        dest.writeString(description);
        dest.writeString(startTime);
        dest.writeString(finishTime);
        dest.writeString(reminderTime);
        dest.writeString(repeat);
        dest.writeString(date);
        dest.writeString(scheduleId);
    }
}

