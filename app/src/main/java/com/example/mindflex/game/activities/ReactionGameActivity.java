package com.example.mindflex.game.activities;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mindflex.HapticFeedbackManager;
import com.example.mindflex.R;
import com.example.mindflex.database.DailyActivityManager;
import com.example.mindflex.database.HighScoreManager;

import android.os.Handler;

import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {

    private View rootView;
    private View overlayView;
    private CardView startScreen;
    private LinearLayout gameScreen;
    private TextView instructionText;
    private TextView reactionGameOverTime;
    private TextView reactionGameOverText;
    private CardView gameOverScreen;
    private Button startButton;

    private Button reactionGameOverBack;
    private Button reactionGameOverRetry;

    private final Handler handler = new Handler();
    private long startTime;
    private boolean waitingForTap = false;
    private boolean activeRound = false;

    private int highScore = 0;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reaction_game);

        rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        rootView = findViewById(R.id.reaction_root);
        overlayView = findViewById(R.id.reaction_overlay);
        startScreen = findViewById(R.id.reaction_start);
        gameScreen = findViewById(R.id.reaction_game_screen);
        instructionText = findViewById(R.id.reaction_instruction);
        startButton = findViewById(R.id.reaction_start_button);

        reactionGameOverTime = findViewById(R.id.reaction_game_over_time);
        reactionGameOverText = findViewById(R.id.reaction_game_over_text);
        gameOverScreen = findViewById(R.id.reaction_game_over);
        reactionGameOverBack = findViewById(R.id.reaction_game_over_back);
        reactionGameOverRetry = findViewById(R.id.reaction_game_over_restart);

        HighScoreManager.getHighScore(this, "Reaction Game", score -> runOnUiThread(() -> highScore = score));


        startButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(startScreen, 300);
            fadeOut(overlayView, 300);
            fadeIn(gameScreen, 300);
            rootView.postDelayed(this::startReactionRound, 300);
        });

        rootView.setOnClickListener(v -> {
            if (!activeRound) return;

            HapticFeedbackManager.HapticFeedbackStrong(v);
            if (waitingForTap) {
                long reactionTime = System.currentTimeMillis() - startTime;
                waitingForTap = false;
                activeRound = false;
                v.setBackgroundColor(getColor(R.color.reaction_background));

                fadeIn(gameOverScreen, 300);
                fadeIn(overlayView, 300);
                instructionText.setText("");
                reactionGameOverText.setText("Nice!\nReaction time");
                reactionGameOverTime.setText(reactionTime +"ms");
                reactionGameOverTime.setVisibility(View.VISIBLE);

                if ( (int) reactionTime < highScore){
                    highScore = (int) reactionTime ;
                    HighScoreManager.insertHighScore(this, "Reaction Game", highScore);
                }

            } else {
                cancelReactionRound();
                v.setBackgroundColor(getColor(R.color.reaction_background));

                fadeIn(gameOverScreen, 300);
                fadeIn(overlayView, 300);
                instructionText.setText("");
                reactionGameOverText.setText("Too early!");
                reactionGameOverTime.setVisibility(View.GONE);
            }

            DailyActivityManager.RecordGame(this, "Reaction Game");

            reactionGameOverBack.setOnClickListener(vv->{
                HapticFeedbackManager.HapticFeedbackLight(vv);
                onBackPressed();
            });

            reactionGameOverRetry.setOnClickListener(vv->{
                HapticFeedbackManager.HapticFeedbackLight(vv);
                fadeOut(gameOverScreen, 300);
                fadeOut(overlayView, 300);
                rootView.postDelayed(this::startReactionRound, 300);
            });

        });

    }

    @SuppressLint("SetTextI18n")
    private void startReactionRound() {
        instructionText.setText("Wait for green...");
        rootView.setBackgroundColor(getColor(R.color.red));
        waitingForTap = false;
        activeRound = true;

        int delay = 2000 + new Random().nextInt(3000);
        handler.postDelayed(() -> {
            if (!activeRound) return;
            instructionText.setText("Tap now!");
            rootView.setBackgroundColor(getColor(R.color.green));
            startTime = System.currentTimeMillis();
            waitingForTap = true;
        }, delay);
    }

    private void cancelReactionRound() {
        handler.removeCallbacksAndMessages(null);
        waitingForTap = false;
        activeRound = false;
    }

    private void fadeIn(View view, int duration) {
        view.animate().alpha(1f).setDuration(duration).withEndAction(() -> view.setVisibility(View.VISIBLE)).start();
    }

    private void fadeOut(View view, int duration) {
        view.animate().alpha(0f).setDuration(duration).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }

}