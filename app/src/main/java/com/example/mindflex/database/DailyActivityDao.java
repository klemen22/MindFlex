package com.example.mindflex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DailyActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivity(DailyActivity activity);

    @Query("UPDATE DailyActivity SET timesPlayed = timesPlayed + 1 WHERE gameId = :gameID AND date = :date")
    void countActivity(String gameID, String date);

    @Query("SELECT * FROM DailyActivity WHERE gameId = :gameID AND date = :date LIMIT 1")
    DailyActivity getActivityForDay(String gameID, String date);

    @Query("SELECT * FROM dailyactivity WHERE gameId = :gameID ORDER BY date ASC")
    List<DailyActivity> getAllActivitiesForGame(String gameID);

    @Query("SELECT * FROM DailyActivity ORDER BY date ASC")
    List<DailyActivity> getAllActivities();

    @Query("DELETE FROM DailyActivity")
    void deleteAllActivityEntries();
}
