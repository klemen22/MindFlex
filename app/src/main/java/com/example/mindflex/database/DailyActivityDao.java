package com.example.mindflex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// interface for reading and writing stuff into the daily activity table
@Dao
public interface DailyActivityDao {

    // insert / increment activities in the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivity(DailyActivity activity);

    @Query("UPDATE DailyActivity SET timesPlayed = timesPlayed + 1 WHERE gameId = :gameID AND date = :date")
    void countActivity(String gameID, String date);

    // retrieve data for a specific dat and activity
    @Query("SELECT * FROM DailyActivity WHERE gameId = :gameID AND date = :date LIMIT 1")
    DailyActivity getActivityForDay(String gameID, String date);

    // retrieve all days for a specific activity
    @Query("SELECT * FROM dailyactivity WHERE gameId = :gameID ORDER BY date ASC")
    List<DailyActivity> getAllActivitiesForGame(String gameID);

    // retrieve all days for a specific activity
    @Query("SELECT * FROM DailyActivity ORDER BY date ASC")
    List<DailyActivity> getAllActivities();

    // delete all entries (for resetting table)
    @Query("DELETE FROM DailyActivity")
    void deleteAllActivityEntries();
}
