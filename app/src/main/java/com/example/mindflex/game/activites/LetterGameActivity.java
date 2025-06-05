package com.example.mindflex.game.activites;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mindflex.R;
import com.example.mindflex.database.DailyActivityManager;
import com.example.mindflex.database.HighScoreManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class LetterGameActivity extends AppCompatActivity {

    private TextView wordText, scoreText, livesText, highscoreText;
    private Button seenButton, newButton, startButton;
    private CardView startCard;
    private View overlay, gameScreen;

    private List<String> wordPool;
    private Set<String> seenWords = new HashSet<>();
    private Random random = new Random();

    private String currentWord = "";
    private int score = 0;
    private int lives = 3;
    private int highscore = 0;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_letter_game);

        wordText = findViewById(R.id.letter_word);
        scoreText = findViewById(R.id.letter_score_text);
        livesText = findViewById(R.id.letter_lives_text);
        seenButton = findViewById(R.id.letter_button_seen);
        newButton = findViewById(R.id.letter_button_new);
        startButton = findViewById(R.id.letter_start_button);
        highscoreText = findViewById(R.id.letter_game_highscore);
        startCard = findViewById(R.id.letter_start);
        overlay = findViewById(R.id.letter_overlay);
        gameScreen = findViewById(R.id.letter_game_screen);

        wordPool = loadWordsFromAssets();
        Collections.shuffle(wordPool);

        HighScoreManager.getHighScore(this, "Letter Game", score -> {
            highscore = score;
            highscoreText.setText("Highscore: " + highscore);
        });

        startButton.setOnClickListener(v -> {
            startCard.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            gameScreen.setVisibility(View.VISIBLE);
            startGame();
        });

        seenButton.setOnClickListener(v -> handleGuess(true));
        newButton.setOnClickListener(v -> handleGuess(false));
    }

    private void startGame() {
        score = 0;
        lives = 3;
        seenWords.clear();
        updateUI();
        showNextWord();
    }

    private void showNextWord() {
        // Probability of repeat increases with score - adjustable
        float repeatChance = Math.min(0.3f + score * 0.01f, 0.4f); // starts 30%, caps at 50%
        boolean showSeenWord = !seenWords.isEmpty() && random.nextFloat() < repeatChance;

        if (showSeenWord) {
            int index = random.nextInt(seenWords.size());
            List<String> seenList = new ArrayList<>(seenWords);
            seenList.remove(currentWord); // avoid immediate repeat
            if (seenList.isEmpty()) {
                currentWord = wordPool.remove(0);
            } else {
                currentWord = seenList.get(random.nextInt(seenList.size()));
            }
        } else {
            if (wordPool.isEmpty()) {
                wordPool = loadWordsFromAssets();
                Collections.shuffle(wordPool);
            }
            currentWord = wordPool.remove(0);
        }

        wordText.setText(currentWord);
    }

    private void handleGuess(boolean guessedSeen) {
        boolean isActuallySeen = seenWords.contains(currentWord);

        if (guessedSeen == isActuallySeen) {
            score++;
        } else {
            lives--;
        }

        seenWords.add(currentWord);
        updateUI();

        if (lives <= 0) {
            endGame();
        } else {
            new Handler().postDelayed(this::showNextWord, 200);
        }
    }

    private void updateUI() {
        scoreText.setText("Score | " + score);
        livesText.setText("Lives | " + lives);
    }

    private void endGame() {
        seenButton.setEnabled(false);
        newButton.setEnabled(false);
        wordText.setText("Game Over");

        if (score > highscore) {
            HighScoreManager.insertHighScore(this, "Letter Game", score);
        }

        DailyActivityManager.RecordGame(this, "Letter Game");

        new Handler().postDelayed(() -> {
            seenButton.setEnabled(true);
            newButton.setEnabled(true);
            startCard.setVisibility(View.VISIBLE);
            gameScreen.setVisibility(View.GONE);
        }, 2000);
    }

    private List<String> loadWordsFromAssets() {
        List<String> words = new ArrayList<>();
        try {
            InputStream is = getAssets().open("word_list.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) words.add(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }
}
