package com.example.mindflex.database;
import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreInitialize {
    private static final String[] game_ids = {"Chimp Game", "Verbal Game", "Number Mame",
                                              "Reaction Game", "Sequence Game", "Typing Game"};
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static void initializeScores (Context context){
        executorService.execute(()->{
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            HighScoreDao highScoreDao = appDatabase.highScoreDao();
            for (String gameID : game_ids){
                HighScore exists = highScoreDao.getScore(gameID);
                if(exists == null){
                    highScoreDao.insertScore(new HighScore(gameID, 0));
                }
            }
        });
    }
}
