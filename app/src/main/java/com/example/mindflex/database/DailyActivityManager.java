package com.example.mindflex.database;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyActivityManager {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // save one instance of playing a certain game
    public static void RecordGame(Context context, String gameID) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            String date = getDate();

            DailyActivity dailyActivity = dailyActivityDao.getActivityForDay(gameID, date);
            // check if a game activity entry already exists
            if (dailyActivity != null) {
                dailyActivityDao.countActivity(gameID, date);
            } else {
                dailyActivityDao.insertActivity(new DailyActivity(gameID, date, 1));
            }
        });
    }

    // read game activity for a specific game
    public static void getGameActivities(Context context, String gameID, DailyActivityCallback callback) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            List<DailyActivity> activityList = dailyActivityDao.getAllActivitiesForGame(gameID);
            callback.onResult(activityList);
        });
    }

    // read game activity for all games
    public static void getAllGamesActivities(Context context, DailyActivityCallback callback) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            List<DailyActivity> activities = dailyActivityDao.getAllActivities();
            callback.onResult(activities);
        });

    }

    // for debugging
    public static void printAllActivities(Context context) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            List<DailyActivity> activityList = dailyActivityDao.getAllActivities();
            Log.d("ActivityList", "Activity list:");
            for (DailyActivity activity : activityList) {
                Log.d("ActivityList", "Game: " + activity.gameID + " | Date: " + activity.date + " | Times played: " + activity.timesPlayed);
            }

        });
    }

    // delete all entries
    public static void deleteAllActivities(Context context) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            dailyActivityDao.deleteAllActivityEntries();
        });
    }

    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public interface DailyActivityCallback {
        void onResult(List<DailyActivity> activities);
    }

}
