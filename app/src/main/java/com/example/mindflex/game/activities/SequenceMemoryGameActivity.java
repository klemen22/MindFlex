package com.example.mindflex.game.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mindflex.HapticFeedbackManager;
import com.example.mindflex.R;
import com.example.mindflex.database.DailyActivityManager;
import com.example.mindflex.database.HighScoreManager;

import java.util.ArrayList;
import java.util.List;

public class SequenceMemoryGameActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView scoreText;
    private final List<View> tiles = new ArrayList<>();
    private final int tileSize = 240;
    private int round = 0;
    private int highScore = 0;

    private final List<Integer> tilesSequence = new ArrayList<>();
    private int currentIndex = 0;

    private boolean input = false;
    private boolean gameFlag = false;
    private boolean gameMenuOpen = false;
    private CardView startScreen;
    private CardView gameOverScreen;
    private Button startButton;
    private Button retryButton;
    private Button quitButton;
    private View overlay;
    private LinearLayout sequenceMenu;
    private LinearLayout sequenceMain;
    private ImageView sequenceMenuBack;
    private ImageView sequenceMenuPlay;
    private ImageView sequenceMenuRestart;
    private ImageView sequenceMenuButton;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sequence_memory_game);

        HighScoreManager.getHighScore(this, "Sequence Game", score -> runOnUiThread(() -> {
            highScore = score;
            Log.d("highscore", "Highscore: " + highScore);
        }));

        View rootView = findViewById(android.R.id.content);

        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        gridLayout = findViewById(R.id.sequence_game_grid);
        scoreText = findViewById(R.id.sequence_game_score);
        startScreen = findViewById(R.id.sequence_start);
        gameOverScreen = findViewById(R.id.sequence_game_over);
        startButton = findViewById(R.id.sequence_start_button);
        retryButton = findViewById(R.id.sequence_play_again_button);
        quitButton = findViewById(R.id.sequence_back_button);
        overlay = findViewById(R.id.sequence_overlay);
        sequenceMain = findViewById(R.id.sequence_game_main);

        sequenceMenu = findViewById(R.id.sequence_game_menu);
        sequenceMenuBack = findViewById(R.id.sequence_game_menu_back_button);
        sequenceMenuPlay = findViewById(R.id.sequence_game_menu_play_button);
        sequenceMenuRestart = findViewById(R.id.sequence_game_menu_retry_button);
        sequenceMenuButton = findViewById(R.id.sequence_game_menu_button);

        startButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            startScreen.setVisibility(View.GONE);
            gameOverScreen.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            round = 0;
            scoreText.setText("Level: " + (round + 1));
            startRound();
            gameFlag = true;
        });

        sequenceMenuButton.setOnClickListener(v -> {
            if (!gameFlag) {
                return;
            }
            gameMenuOpen = true;
            input = false;
            HapticFeedbackManager.HapticFeedbackLight(v);

            overlay.animate().alpha(1f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.VISIBLE)).start();
            sequenceMenuButton.animate().alpha(0f).setDuration(300).withEndAction(() -> sequenceMenuButton.setVisibility(View.GONE)).start();
            sequenceMenu.setVisibility(View.VISIBLE);
            sequenceMenu.setTranslationY(sequenceMenu.getHeight());
            sequenceMain.animate().translationY(-80).setDuration(300).start();
            sequenceMenu.animate().translationY(0).setDuration(300).start();

            sequenceMenuBack.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                onBackPressed();
            });

            sequenceMenuPlay.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                input = true;
                gameMenuOpen = false;
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.GONE)).start();
                sequenceMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> sequenceMenuButton.setVisibility(View.VISIBLE)).start();
                sequenceMain.animate().translationY(0).setDuration(300).start();
                sequenceMenu.animate().translationY(sequenceMenu.getHeight()).setDuration(300).withEndAction(() -> sequenceMenu.setVisibility(View.GONE)).start();
            });

            sequenceMenuRestart.setOnClickListener(vv -> {
                gameMenuOpen = false;
                HapticFeedbackManager.HapticFeedbackLight(vv);
                Toast.makeText(this, "Restarting...", Toast.LENGTH_SHORT).show();
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.GONE)).start();
                sequenceMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> sequenceMenuButton.setVisibility(View.VISIBLE)).start();
                sequenceMain.animate().translationY(0).setDuration(300).start();
                sequenceMenu.animate().translationY(sequenceMenu.getHeight()).setDuration(300).withEndAction(() -> sequenceMenu.setVisibility(View.GONE)).start();
                round = 0;
                scoreText.setText("Level: " + (round + 1));
                startRound();
            });

        });

        for (int i = 0; i < 16; i++) {
            View tile = new View(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tileSize;
            params.height = tileSize;
            params.setMargins(5, 5, 5, 5);
            tile.setLayoutParams(params);
            tile.setBackgroundResource(R.drawable.sequence_tile_background);
            tile.setElevation(5);

            final int index = i;
            tile.setOnClickListener(v -> {
                if (!input) {
                    return;
                }
                HapticFeedbackManager.HapticFeedbackStrong(v);

                if (index == tilesSequence.get(currentIndex)) {
                    tile.animate().alpha(1f).setDuration(200).withEndAction(() -> tile.setBackgroundResource(R.drawable.sequence_tile_background_correct)).start();

                    currentIndex++;

                    if (currentIndex == tilesSequence.size()) {
                        round++;
                        scoreText.setText("Level: " + (round + 1));
                        rootView.postDelayed(this::startRound, 600);
                    }

                } else {
                    tile.animate().alpha(1f).setDuration(200).withEndAction(() -> tile.setBackgroundResource(R.drawable.sequence_tile_background_error)).start();
                    input = false;
                    gameOverScreen.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.VISIBLE);

                    if (round > highScore) {
                        HighScoreManager.insertHighScore(this, "Sequence Game", round);
                    }

                    DailyActivityManager.RecordGame(this, "Sequence Game");

                    retryButton.setOnClickListener(v1 -> {
                        HapticFeedbackManager.HapticFeedbackLight(v1);
                        for (View tile1 : tiles) {
                            tile1.setBackgroundResource(R.drawable.sequence_tile_background);
                        }
                        gameOverScreen.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        round = 0;
                        scoreText.setText("Level: " + (round + 1));
                        rootView.postDelayed(this::startRound, 600);
                    });
                    quitButton.setOnClickListener(v1 -> {
                        HapticFeedbackManager.HapticFeedbackLight(v1);
                        onBackPressed();
                    });
                }
            });

            tiles.add(tile);
            gridLayout.addView(tile);

        }

    }

    private void startRound() {
        for (View tile : tiles) {
            tile.setBackgroundResource(R.drawable.sequence_tile_background);
        }
        tilesSequence.clear();
        currentIndex = 0;

        if (round >= 50) {
            input = false;
            gameOverScreen.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i < round + 1; i++) {
            int randomNum = (int) (Math.random() * 16);
            tilesSequence.add(randomNum);
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::showSequence, 800);
    }


    private void showSequence() {
        input = false;

        for (int i = 0; i < tilesSequence.size(); i++) {
            int index = tilesSequence.get(i);
            View tile = tiles.get(index);

            tile.postDelayed(() -> {
                tile.setAlpha(0f);
                tile.animate().alpha(1f).setDuration(200).start();
                tile.setBackgroundResource(R.drawable.sequence_tile_background_correct);
            }, i * 800L);

            int finalI = i;
            tile.postDelayed(() -> {
                tile.setAlpha(1f);
                tile.animate().alpha(0f).setDuration(200).start();
                tile.animate().alpha(1f).setDuration(200).withEndAction(() -> tile.setBackgroundResource(R.drawable.sequence_tile_background)).start();

                if ((finalI == tilesSequence.size() - 1) && !gameMenuOpen) {
                    input = true;
                }
            }, i * 800L + 800);

        }
    }
}