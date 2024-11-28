package com.example.ontimeapp;

public class ScheduleHistory {

    private String name, place, description, startTime, finishTime, reminderTime, repeat, date, imageUrl;

    public ScheduleHistory() {
        // Default constructor required for Firebase
    }

    public ScheduleHistory(String name, String place, String description, String startTime, String finishTime, String reminderTime, String repeat, String date, String imageUrl) {
        this.name = name;
        this.place = place;
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.reminderTime = reminderTime;
        this.repeat = repeat;
        this.date = date;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}