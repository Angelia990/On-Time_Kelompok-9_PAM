package com.example.ontimeapp;

public class Note {
    String noteId, name, desc, date;
    Boolean pinned = false;

    public Note() {
    }

    public Note(String noteId, String name, String desc, String date, Boolean pinned) {
        this.noteId = noteId;
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.pinned = pinned;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }
}
