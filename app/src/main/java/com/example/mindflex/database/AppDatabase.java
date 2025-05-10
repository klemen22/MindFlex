package com.example.mindflex.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HighScore.class, DailyActivity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static  AppDatabase instance;
    public abstract HighScoreDao highScoreDao();
    public abstract DailyActivityDao dailyActivityDao();

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "GameData.db").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
