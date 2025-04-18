package com.example.mindflex.game.activites;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindflex.R;

import java.util.ArrayList;
import java.util.List;

public class SequenceMemoryGameActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView scoreText;
    private List<View> tiles = new ArrayList<>();
    private int tileSize = 240;
    private int round = 0;

    private List<Integer> tilesSequence = new ArrayList<>();
    private int currentIndex = 0;

    private boolean input = false;
    private LinearLayout startScreen;
    private LinearLayout gameOverScreen;
    private Button startButton;
    private Button retryButton;
    private Button quitButton;
    private View overlay;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sequence_memory_game);

        //fix screen space
        View rootView = findViewById(android.R.id.content);

        // for now bottom and top part of the screen space will be limited
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        Button backbutton = findViewById(R.id.sequence_game_back);

        backbutton.setOnClickListener(v -> {
            onBackPressed();
        });

        // xml elements initialization
        gridLayout = findViewById(R.id.sequence_game_grid);
        scoreText = findViewById(R.id.sequence_game_score);
        startScreen = findViewById(R.id.sequence_start);
        gameOverScreen = findViewById(R.id.sequence_game_over);
        startButton = findViewById(R.id.sequence_start_button);
        retryButton = findViewById(R.id.sequence_play_again_button);
        quitButton = findViewById(R.id.sequence_back_button);
        overlay = findViewById(R.id.sequence_overlay);

        startButton.setOnClickListener(v -> {
            startScreen.setVisibility(View.GONE);
            gameOverScreen.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            round = 0;
            scoreText.setText("Level: "+ (round + 1));
            startRound();
        });

        for (int i = 0; i < 16; i++){
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
                if(input == false){
                    return;
                }

                // case of correct click
                if(index == tilesSequence.get(currentIndex)){
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tile, "alpha", 0f, 1f);
                    fadeIn.setDuration(100);
                    fadeIn.start();

                    tile.setBackgroundResource(R.drawable.sequence_tile_background_correct);
                    currentIndex++;

                    // round completed
                    if(currentIndex == tilesSequence.size()){
                        round++;
                        scoreText.setText("Level: "+ (round + 1));
                        rootView.postDelayed(this::startRound, 600);
                    }

                }
                // case of incorrect click
                else{
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tile, "alpha", 0f, 1f);
                    fadeIn.setDuration(100);
                    fadeIn.start();
                    tile.setBackgroundResource(R.drawable.sequence_tile_background_error);
                    input = false;
                    gameOverScreen.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.VISIBLE);
                    retryButton.setOnClickListener(v1 -> {
                        for(View tile1 : tiles){
                            tile1.setBackgroundResource(R.drawable.sequence_tile_background);
                        }
                        gameOverScreen.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        round = 0;
                        scoreText.setText("Level: "+ (round + 1));
                        rootView.postDelayed(this::startRound, 600);
                    });
                    quitButton.setOnClickListener(v1 -> {
                        onBackPressed();
                    });
                }
            });

            tiles.add(tile);
            gridLayout.addView(tile);

        }

    }
    private void startRound(){
        // reset tiles
        for(View tile : tiles){
            tile.setBackgroundResource(R.drawable.sequence_tile_background);
        }

        tilesSequence.clear();
        currentIndex = 0;

        for(int i = 0; i < round + 1; i++){
            int randomNum = (int) (Math.random() * 16);
            tilesSequence.add(randomNum);
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::showSequence, 1000);
    }


    private void showSequence(){
        input = false;

        for(int i = 0; i < tilesSequence.size(); i++){
            int index = tilesSequence.get(i);
            View tile = tiles.get(index);

            tile.postDelayed(() -> {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tile, "alpha", 0f, 1f);
                fadeIn.setDuration(200);
                fadeIn.start();

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(tile, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(tile, "scaleY", 0f, 1f);
                scaleX.setDuration(200);
                scaleY.setDuration(200);
                scaleX.start();
                scaleY.start();

                tile.setBackgroundResource(R.drawable.sequence_tile_background_correct);
            }, i * 800);

            int finalI = i;
            tile.postDelayed(()->{
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tile, "alpha", 0f, 1f);
                fadeOut.setDuration(200);
                fadeOut.start();

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(tile, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(tile, "scaleY", 0f, 1f);
                scaleX.setDuration(200);
                scaleY.setDuration(200);
                scaleX.start();
                scaleY.start();


                tile.setBackgroundResource(R.drawable.sequence_tile_background);
                if(finalI == tilesSequence.size() - 1){
                    input = true;
                }
            }, i * 800 + 1000);

        }
    }
}