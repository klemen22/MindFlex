package com.example.mindflex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

// Dao - Data Access Object // interface for reading and writing stuff into the high score table
@Dao
public interface HighScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScore(HighScore highScore);

    @Query("SELECT * FROM HighScore WHERE gameID = :gameID LIMIT 1")
    HighScore getScore(String gameID);
}
