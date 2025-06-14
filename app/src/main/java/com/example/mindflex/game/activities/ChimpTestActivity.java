package com.example.mindflex.game.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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
import java.util.Collections;

public class ChimpTestActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private CardView chimpStartScreen;
    private CardView chimpMidRound;
    private CardView chimpGameOver;
    private LinearLayout chimpGameMenu;
    private LinearLayout chimpMain;
    private View chimpOverlay;
    private TextView chimpMidRoundNum;
    private TextView chimpMidRoundStrike;
    private TextView chimpGameOverScore;
    private TextView chimpGameRound;
    private ImageView chimpMenuButton;
    private ImageView chimpGameMenuBack;
    private ImageView chimpGameMenuRestart;
    private ImageView chimpGameMenuPlay;
    private Button chimpStartButton;
    private Button chimpMidRoundContinue;
    private Button chimpGameOverBack;
    private Button chimpGameOverRestart;

    private int strikes = 3;
    private int round = 0;
    private int highScore = 0;
    private int index = 1;
    private final int rowsNum = 7;
    private final int colsNum = 4;
    private final int tileSize = 240;
    private boolean input = false;
    private boolean gameFlag = false;
    private final ArrayList<Tile> tilesList = new ArrayList<>();


    private static class Tile {
        int number;
        View view;

        Tile(int number, View view) {
            this.number = number;
            this.view = view;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chimp_test);
        View rootView = findViewById(android.R.id.content);

        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        chimpMenuButton = findViewById(R.id.chimp_game_menu_button);
        gridLayout = findViewById(R.id.chimp_test_grid);
        chimpOverlay = findViewById(R.id.chimp_overlay);
        chimpMain = findViewById(R.id.chimp_main_game_screen);
        chimpGameRound = findViewById(R.id.chimp_game_round);
        chimpStartButton = findViewById(R.id.chimp_start_button);
        chimpStartScreen = findViewById(R.id.chimp_start);
        chimpMidRound = findViewById(R.id.chimp_mid_round);
        chimpMidRoundNum = findViewById(R.id.chimp_mid_round_number);
        chimpMidRoundStrike = findViewById(R.id.chimp_mid_round_strikes);
        chimpMidRoundContinue = findViewById(R.id.chimp_mid_round_continue);
        chimpGameOver = findViewById(R.id.chimp_game_over);
        chimpGameOverScore = findViewById(R.id.chimp_game_over_score);
        chimpGameOverBack = findViewById(R.id.chimp_game_over_back);
        chimpGameOverRestart = findViewById(R.id.chimp_game_over_restart);
        chimpGameMenu = findViewById(R.id.chimp_game_menu);
        chimpGameMenuBack = findViewById(R.id.chimp_game_menu_back);
        chimpGameMenuRestart = findViewById(R.id.chimp_game_menu_retry);
        chimpGameMenuPlay = findViewById(R.id.chimp_game_menu_play);

        HighScoreManager.getHighScore(this, "Chimp Game", score -> runOnUiThread(() -> highScore = score));

        chimpStartButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            strikes = 3;
            round = 0;
            gameFlag = true;
            fadeOut(chimpStartScreen, 300);
            fadeOut(chimpOverlay, 300);
            chimpGameOver.setVisibility(View.GONE);
            rootView.postDelayed(this::startRound, 150);
        });

        chimpMenuButton.setOnClickListener(v -> {
            if (!gameFlag) {
                return;
            }
            HapticFeedbackManager.HapticFeedbackLight(v);
            input = false;
            chimpOverlay.animate().alpha(1f).setDuration(300).withEndAction(() -> chimpOverlay.setVisibility(View.VISIBLE)).start();
            chimpMenuButton.animate().alpha(0f).setDuration(300).withEndAction(() -> chimpMenuButton.setVisibility(View.GONE)).start();
            chimpGameMenu.setVisibility(View.VISIBLE);
            chimpGameMenu.setTranslationY(chimpGameMenu.getHeight());
            chimpMain.animate().translationY(-80).setDuration(300).start();
            chimpGameMenu.animate().translationY(0).setDuration(300).start();

            chimpGameMenuBack.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                onBackPressed();
            });

            chimpGameMenuPlay.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                input = true;
                chimpOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> chimpGameMenu.setVisibility(View.GONE)).start();
                chimpMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> chimpMenuButton.setVisibility(View.VISIBLE)).start();
                chimpMain.animate().translationY(0).setDuration(300).start();
                chimpGameMenu.animate().translationY(chimpGameMenu.getHeight()).setDuration(300).withEndAction(() -> chimpGameMenu.setVisibility(View.GONE)).start();
            });

            chimpGameMenuRestart.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                chimpOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> chimpGameMenu.setVisibility(View.GONE)).start();
                chimpMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> chimpMenuButton.setVisibility(View.VISIBLE)).start();
                chimpMain.animate().translationY(0).setDuration(300).start();
                chimpGameMenu.animate().translationY(chimpGameMenu.getHeight()).setDuration(300).withEndAction(() -> chimpGameMenu.setVisibility(View.GONE)).start();
                round = 0;
                strikes = 3;
                Toast.makeText(this, "Restarting...", Toast.LENGTH_SHORT).show();
                rootView.postDelayed(this::startRound, 150);
            });

        });
    }

    @SuppressLint("SetTextI18n")
    private void startRound() {
        if (round == 24) {
            GameOver(round + 4);
        }
        int tilesNum = round + 4;
        index = 1;
        gridLayout.setRowCount(rowsNum);
        gridLayout.setColumnCount(colsNum);
        chimpGameRound.setText("Round " + (round + 1));

        gridLayout.removeAllViews();
        tilesList.clear();

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < rowsNum * colsNum; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        for (int i = 0; i < rowsNum; i++) {
            for (int j = 0; j < colsNum; j++) {
                View tile = new View(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.setMargins(5, 5, 5, 5);
                tile.setLayoutParams(params);
                tile.setBackgroundResource(R.drawable.chimp_test_tile_background);

                TextView textView = new TextView(this);
                textView.setText("");
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setLayoutParams(params);

                tile = textView;
                gridLayout.addView(tile);

                tilesList.add(new Tile(0, textView));

            }
        }

        Collections.shuffle(tilesList);
        for (int i = 0; i < tilesNum; i++) {
            Tile tile = tilesList.get(i);
            tile.number = i + 1;

            ((TextView) tile.view).setText(String.valueOf(tile.number));
            tile.view.setBackgroundResource(R.drawable.chimp_test_tile_background);
            fadeIn(tile.view, 300);


        }
        input = true;
        tileGuessing(tilesNum);

    }

    private void tileGuessing(int tilesNum) {

        for (int i = 0; i < tilesNum; i++) {
            Tile tile = tilesList.get(i);
            tile.view.setOnClickListener(v -> {
                if (!input) return;
                HapticFeedbackManager.HapticFeedbackStrong(v);

                if (tile.number == index) {
                    if (tile.number == 1) {
                        tile.view.setVisibility(View.INVISIBLE);
                        for (int j = 1; j < tilesNum; j++) {
                            Tile tileTemp = tilesList.get(j);
                            ((TextView) tileTemp.view).setText("");
                            tileTemp.view.setBackgroundResource(R.drawable.chimp_test_tile_background_highlight);
                            tileTemp.view.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fadeOut(tile.view, 150);
                    }
                    index++;
                    if (index == tilesNum + 1) {
                        round++;
                        MidRound(round + 1);
                    }
                } else {
                    strikes--;
                    if (strikes == 0) {
                        GameOver(tilesNum);
                    } else {
                        MidRound(round + 1);
                    }
                }
            });
        }

    }

    @SuppressLint("SetTextI18n")
    private void MidRound(int tileNum) {
        View rootView = findViewById(android.R.id.content);
        input = false;
        fadeIn(chimpMidRound, 300);
        fadeIn(chimpOverlay, 300);

        chimpMidRoundNum.setText(String.valueOf(tileNum + 3));
        chimpMidRoundStrike.setText(strikes + " of 3");

        chimpMidRoundContinue.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(chimpMidRound, 300);
            fadeOut(chimpOverlay, 300);
            rootView.postDelayed(this::startRound, 150);
        });
    }

    private void GameOver(int score) {
        View rootView = findViewById(android.R.id.content);
        input = false;
        fadeIn(chimpGameOver, 300);
        fadeIn(chimpOverlay, 300);

        if (round + 4 > highScore) {
            highScore = round + 4;
            HighScoreManager.insertHighScore(this, "Chimp Game", highScore);
        }

        DailyActivityManager.RecordGame(this, "Chimp Game");

        chimpGameOverScore.setText(String.valueOf(score));

        chimpGameOverBack.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            onBackPressed();
        });

        chimpGameOverRestart.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(chimpGameOver, 300);
            fadeOut(chimpOverlay, 300);
            strikes = 3;
            round = 0;
            rootView.postDelayed(this::startRound, 150);
        });
    }


    private void fadeIn(View view, int duration) {
        view.animate().alpha(1f).setDuration(duration).withEndAction(() -> view.setVisibility(View.VISIBLE)).start();
    }

    private void fadeOut(View view, int duration) {
        view.animate().alpha(0f).setDuration(duration).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }


}