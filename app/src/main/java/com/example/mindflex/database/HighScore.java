package com.example.mindflex.database;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Class for high score table
@Entity(tableName = "HighScore")
public class HighScore {
    @PrimaryKey
    @NonNull
    public String gameID;
    public int score;
    public HighScore(@NonNull String gameID, int score){
        this.gameID = gameID;
        this.score = score;
    }
}
