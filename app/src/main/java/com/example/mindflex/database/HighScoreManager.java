package com.example.mindflex.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HighScoreManager {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void insertHighScore(Context context, String gameID, int score){
        executorService.execute(()->{
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            HighScoreDao highScoreDao = appDatabase.highScoreDao();
            highScoreDao.insertScore(new HighScore(gameID, score));
        });
    }

    public static void getHighScore(Context context, String gameID, HighScoreCallback callback){
        executorService.execute(()->{
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            HighScoreDao highScoreDao = appDatabase.highScoreDao();
            HighScore score = highScoreDao.getScore(gameID);
            int scoreFinal = 0;
            if (score != null){
                scoreFinal = score.score;
            }
            callback.onResult(scoreFinal);
        });
    }

    public interface HighScoreCallback{
        void onResult(int score);
    }
}
