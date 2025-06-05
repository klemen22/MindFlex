package com.example.mindflex;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mindflex.database.AppDatabase;
import com.example.mindflex.database.DailyActivityManager;
import com.example.mindflex.database.HighScore;
import com.example.mindflex.database.HighScoreDao;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsScreenActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ScoreItem> scoreItemList = new ArrayList<>();
    private ScoreAdapter scoreAdapter;

    private static final String[] game_ids = {"Chimp Game", "Verbal Game", "Number Mame",
            "Reaction Game", "Sequence Game", "Typing Game"};
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats_screen);

        //fix screen space
        View rootView = findViewById(android.R.id.content);

        // for now bottom and top part of the screen space will be limited
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        ImageView backbutton = findViewById(R.id.stats_back_button);
        Button dailyActivityButton = findViewById(R.id.stats_daily_activity);

        backbutton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            onBackPressed();
        });

        dailyActivityButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            Context context = rootView.getContext();
            Intent intent = new Intent(context, DailyScreenActivity.class);
            context.startActivity(intent);

        });

        recyclerView = findViewById(R.id.scores_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scoreAdapter = new ScoreAdapter(scoreItemList);
        recyclerView.setAdapter(scoreAdapter);
        loadData();

    }

    public void loadData() {
        executorService.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getInstance(this);
            HighScoreDao highScoreDao = appDatabase.highScoreDao();

            List<ScoreItem> loadedScores = new ArrayList<>();

            for (String gameID : game_ids) {
                HighScore highScore = highScoreDao.getScore(gameID);
                int score = 0;
                if (highScore != null) {
                    score = highScore.score;
                }
                loadedScores.add(new ScoreItem(gameID, score));
            }

            runOnUiThread(() -> {
                scoreItemList.clear();
                scoreItemList.addAll(loadedScores);
                scoreAdapter.notifyDataSetChanged();
            });
        });

        Log.d("DailyActivity", "Daily Activity:");
        DailyActivityManager.getAllGamesActivities(this, activities -> {
            for (com.example.mindflex.database.DailyActivity activity : activities) {
                Log.d("DailyActivity", "Game: " + activity.gameID + " | Times played: " + activity.timesPlayed + " | Date: " + activity.date);
            }
        });

    }


}