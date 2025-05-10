package com.example.mindflex.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Class for daily activity table
@Entity(tableName = "DailyActivity")
public class DailyActivity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String gameID;
    public String date;
    public int timesPlayed;

    public DailyActivity(String gameID, String date, int timesPlayed){
        this.gameID = gameID;
        this.date = date;
        this.timesPlayed = timesPlayed;
    }
}
