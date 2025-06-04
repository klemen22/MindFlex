package com.example.mindflex.game.activites;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindflex.R;

import android.os.Handler;

import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {

    private View rootView;
    private View overlayView;
    private View startScreen;
    private View gameScreen;
    private TextView instructionText;
    private TextView resultText;
    private Button retryButton;
    private Button startButton;

    private Handler handler = new Handler();
    private long startTime;
    private boolean waitingForTap = false;
    private boolean activeRound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        rootView = findViewById(R.id.reaction_root);
        overlayView = findViewById(R.id.reaction_overlay);
        startScreen = findViewById(R.id.reaction_start);
        gameScreen = findViewById(R.id.reaction_game_screen);
        instructionText = findViewById(R.id.reaction_instruction);
        resultText = findViewById(R.id.reaction_result);
        retryButton = findViewById(R.id.reaction_retry);
        startButton = findViewById(R.id.reaction_start_button);

        startButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startReactionRound();
        });

        rootView.setOnClickListener(v -> {
            if (!activeRound) return;

            if (waitingForTap) {
                long reactionTime = System.currentTimeMillis() - startTime;
                instructionText.setText("Nice!");
                resultText.setText(reactionTime + " ms");
                resultText.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                waitingForTap = false;
                activeRound = false;
                rootView.setBackgroundColor(getColor(android.R.color.white));
            } else {
                // Too early
                cancelReactionRound();
                instructionText.setText("Too soon!");
                resultText.setText("Try again");
                resultText.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                rootView.setBackgroundColor(getColor(android.R.color.white));
            }
        });

        retryButton.setOnClickListener(v -> {
            resultText.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            startReactionRound();
        });
    }

    private void startReactionRound() {
        instructionText.setText("Wait for green...");
        rootView.setBackgroundColor(getColor(R.color.red));
        waitingForTap = false;
        activeRound = true;

        int delay = 2000 + new Random().nextInt(3000); // 2  to 5 sec
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
}