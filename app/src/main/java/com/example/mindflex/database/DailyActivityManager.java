package com.example.mindflex.database;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyActivityManager {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void RecordGame(Context context, String gameID) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            String date = getDate();

            DailyActivity dailyActivity = dailyActivityDao.getActivityForDay(gameID, date);
            if (dailyActivity != null) {
                dailyActivityDao.countActivity(gameID, date);
            } else {
                dailyActivityDao.insertActivity(new DailyActivity(gameID, date, 1));
            }
        });
    }

    public static void getAllGamesActivities(Context context, DailyActivityCallback callback) {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            DailyActivityDao dailyActivityDao = appDatabase.dailyActivityDao();
            List<DailyActivity> activities = dailyActivityDao.getAllActivities();
            callback.onResult(activities);
        });

    }

    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public interface DailyActivityCallback {
        void onResult(List<DailyActivity> activities);
    }

}
