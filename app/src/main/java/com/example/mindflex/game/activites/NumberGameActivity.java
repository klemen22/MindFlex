package com.example.mindflex.game.activites;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;


import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mindflex.R;
import com.example.mindflex.database.DailyActivityManager;
import com.example.mindflex.database.HighScoreManager;

import java.util.Random;

public class NumberGameActivity extends AppCompatActivity {

    private CardView startScreen;
    private View overlay;
    private View gameScreen;

    private ProgressBar timerBar;
    private ValueAnimator timerAnimator;


    private TextView instructionText;
    private TextView displayText;
    private TextView feedbackText;
    private EditText inputField;
    private Button startButton, submitButton, retryButton;

    private int level = 1;
    private String currentNumber = "";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private int highScore = 0;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_number_game);

        // Views
        timerBar = findViewById(R.id.number_timer_bar);
        startScreen = findViewById(R.id.number_start);
        overlay = findViewById(R.id.number_overlay);
        gameScreen = findViewById(R.id.number_game_screen);
        instructionText = findViewById(R.id.number_instruction);
        displayText = findViewById(R.id.number_display);
        feedbackText = findViewById(R.id.number_feedback);
        inputField = findViewById(R.id.number_input);
        startButton = findViewById(R.id.number_start_button);
        submitButton = findViewById(R.id.number_submit);
        retryButton = findViewById(R.id.number_retry);
        TextView highscoreText = findViewById(R.id.number_game_highscore);

        // Get high score
        HighScoreManager.getHighScore(this, "Number Game", score -> {
            highScore = score;
            highscoreText.setText("Highscore: " + highScore);
        });

        startButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            level = 1;
            startRound();
        });

        submitButton.setOnClickListener(v -> {
            String input = inputField.getText().toString().trim();
            if (input.equals(currentNumber)) {
                level++;
                feedbackText.setVisibility(View.VISIBLE);
                feedbackText.setText("Correct!");
                handler.postDelayed(this::startRound, 1000);
            } else {
                showGameOver(input);
            }
        });

        retryButton.setOnClickListener(v -> {
            feedbackText.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            inputField.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            level = 1;
            startRound();
        });
    }

    private void startRound() {
        inputField.setText("");
        inputField.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        feedbackText.setVisibility(View.GONE);

        instructionText.setText("Memorize this number:");
        currentNumber = generateNumber(level);
        displayText.setText(currentNumber);
        timerBar.setVisibility(View.VISIBLE);
        timerBar.setProgress(1000);

        displayText.setVisibility(View.VISIBLE);

        // Show number then hide and prompt input
        int displayTime = 1000 + (level * 800);
        timerBar.setVisibility(View.VISIBLE);
        timerBar.setMax(1000);
        timerBar.setProgress(1000);

        // Cancel previous animator if running
        if (timerAnimator != null && timerAnimator.isRunning()) {
            timerAnimator.cancel();
        }

        timerAnimator = ValueAnimator.ofInt(1000, 0);
        timerAnimator.setDuration(displayTime);
        timerAnimator.setInterpolator(new LinearInterpolator());
        timerAnimator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            timerBar.setProgress(progress);
        });
        timerAnimator.start();

        handler.postDelayed(() -> {
            displayText.setVisibility(View.GONE);
            timerBar.setVisibility(View.GONE);
            instructionText.setText("Enter the number:");
            inputField.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        }, displayTime);
    }

    private void showGameOver(String userInput) {
        instructionText.setText("Wrong! Correct was: " + currentNumber);
        feedbackText.setVisibility(View.VISIBLE);
        feedbackText.setText("You entered: " + userInput + "\nFinal Score: " + (level - 1));
        retryButton.setVisibility(View.VISIBLE);
        inputField.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);

        // Save score
        if (level - 1 > highScore) {
            HighScoreManager.insertHighScore(this, "Number Game", level - 1);
        }

        // Record game
        DailyActivityManager.RecordGame(this, "Number Game");
    }

    private String generateNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerAnimator != null) timerAnimator.cancel();
    }

}
