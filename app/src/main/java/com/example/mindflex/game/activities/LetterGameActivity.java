package com.example.mindflex.game.activities;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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
    private CardView letterGameOverScreen;
    private TextView letterGameOverScore;
    private Button letterGameOverBack;
    private Button letterGameOverRestart;
    private ImageView letterGameMenuButton;
    private LinearLayout letterGameMenu;
    private ImageView letterGameMenuBack;
    private ImageView letterGameMenuPlay;
    private ImageView letterGameMenuRestart;
    private List<String> wordPool;
    private final Set<String> seenWords = new HashSet<>();
    private final Random random = new Random();

    private String currentWord = "";
    private int score = 0;
    private int lives = 3;
    private int highscore = 0;
    View rootView;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_letter_game);

        rootView = findViewById(android.R.id.content);

        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

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

        letterGameOverScreen = findViewById(R.id.letter_game_over);
        letterGameOverScore = findViewById(R.id.letter_game_over_score);
        letterGameOverBack = findViewById(R.id.letter_game_over_back);
        letterGameOverRestart = findViewById(R.id.letter_game_over_restart);

        letterGameMenuButton = findViewById(R.id.letter_game_menu_button);
        letterGameMenu = findViewById(R.id.letter_game_menu);
        letterGameMenuBack = findViewById(R.id.letter_game_menu_back);
        letterGameMenuPlay = findViewById(R.id.letter_game_menu_play);
        letterGameMenuRestart = findViewById(R.id.letter_game_menu_retry);

        wordPool = loadWordsFromAssets();
        Collections.shuffle(wordPool);

        HighScoreManager.getHighScore(this, "Verbal Game", score -> {
            highscore = score;
            highscoreText.setText("Highscore: " + highscore);
        });

        startButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(startCard, 300);
            fadeOut(overlay, 300);
            fadeIn(gameScreen, 300);
            startGame();
        });

        seenButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            handleGuess(true);
        });
        newButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            handleGuess(false);
        });

        letterGameMenuButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            enableInputs(false);
            overlay.animate().alpha(1f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.VISIBLE)).start();
            letterGameMenuButton.animate().alpha(0f).setDuration(300).withEndAction(() -> letterGameMenuButton.setVisibility(View.GONE)).start();
            letterGameMenu.setVisibility(View.VISIBLE);
            letterGameMenu.setTranslationY(letterGameMenu.getHeight());
            gameScreen.animate().translationY(-80).setDuration(300).start();
            letterGameMenu.animate().translationY(0).setDuration(300).start();

            letterGameMenuBack.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                onBackPressed();
            });

            letterGameMenuPlay.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                enableInputs(true);
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> letterGameMenu.setVisibility(View.GONE)).start();
                letterGameMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> letterGameMenuButton.setVisibility(View.VISIBLE)).start();
                gameScreen.animate().translationY(0).setDuration(300).start();
                letterGameMenu.animate().translationY(letterGameMenu.getHeight()).setDuration(300).withEndAction(() -> letterGameMenu.setVisibility(View.GONE)).start();
            });

            letterGameMenuRestart.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                enableInputs(true);
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> letterGameMenu.setVisibility(View.GONE)).start();
                letterGameMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> letterGameMenuButton.setVisibility(View.VISIBLE)).start();
                gameScreen.animate().translationY(0).setDuration(300).start();
                letterGameMenu.animate().translationY(letterGameMenu.getHeight()).setDuration(300).withEndAction(() -> letterGameMenu.setVisibility(View.GONE)).start();
                Toast.makeText(this, "Restarting...", Toast.LENGTH_SHORT).show();
                startGame();
            });

        });
    }

    private void startGame() {
        score = 0;
        lives = 3;
        seenWords.clear();
        updateUI();
        showNextWord();
    }

    private void showNextWord() {
        float repeatChance = Math.min(0.3f + score * 0.01f, 0.4f);
        boolean showSeenWord = !seenWords.isEmpty() && random.nextFloat() < repeatChance;

        if (showSeenWord) {
            int index = random.nextInt(seenWords.size());
            List<String> seenList = new ArrayList<>(seenWords);
            seenList.remove(currentWord);
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

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        scoreText.setText("Score | " + score);
        livesText.setText("Lives | " + lives);
    }

    private void endGame() {
        fadeOut(gameScreen, 300);
        fadeIn(letterGameOverScreen, 300);
        fadeIn(overlay, 300);
        letterGameOverScore.setText(String.valueOf(score));

        if (score > highscore) {
            HighScoreManager.insertHighScore(this, "Verbal Game", score);
        }

        DailyActivityManager.RecordGame(this, "Verbal Game");

        letterGameOverBack.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            onBackPressed();
        });

        letterGameOverRestart.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(letterGameOverScreen, 300);
            fadeOut(overlay, 300);
            fadeIn(gameScreen, 300);
            startGame();
        });

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

    private void fadeIn(View view, int duration) {
        view.animate().alpha(1f).setDuration(duration).withEndAction(() -> view.setVisibility(View.VISIBLE)).start();
    }

    private void fadeOut(View view, int duration) {
        view.animate().alpha(0f).setDuration(duration).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }

    private void enableInputs(boolean flag){
        seenButton.setEnabled(flag);
        newButton.setEnabled(flag);
    }
}
