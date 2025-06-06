package com.example.mindflex.game.activites;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.mindflex.HapticFeedbackManager;
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

    private CardView numberGameOverScreen;
    private TextView numberGameOverScore;

    private Button numberGameOverBack;
    private Button numberGameOverRetry;
    private LinearLayout numberGameMenu;
    private ImageView numberGameMenuBack;
    private ImageView numberGameMenuPlay;
    private ImageView numberGameMenuRestart;
    private ImageView numberGameMenuButton;
    private TextView numberGameOverText;
    private TextView instructionText;
    private TextView displayText;
    private TextView feedbackText;
    private EditText inputField;
    private Button startButton, submitButton;
    private ScrollView numberMainScreen;

    private int level = 1;
    private String currentNumber = "";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private int highScore = 0;
    InputMethodManager imm;
    View rootView;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_number_game);
        rootView = findViewById(android.R.id.content);

        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        numberGameOverScreen = findViewById(R.id.number_game_over);
        numberGameOverScore = findViewById(R.id.number_game_over_score);
        numberGameOverBack = findViewById(R.id.number_game_over_back);
        numberGameOverRetry = findViewById(R.id.number_game_over_restart);
        numberGameOverText = findViewById(R.id.number_game_over_text);
        numberGameMenuButton = findViewById(R.id.number_game_menu_button);
        numberGameMenu = findViewById(R.id.number_game_menu);
        numberGameMenuBack = findViewById(R.id.number_game_menu_back);
        numberGameMenuPlay = findViewById(R.id.number_game_menu_play);
        numberGameMenuRestart = findViewById(R.id.number_game_menu_retry);
        numberMainScreen = findViewById(R.id.number_main_screen);

        TextView highscoreText = findViewById(R.id.number_game_highscore);

        HighScoreManager.getHighScore(this, "Number Game", score -> {
            highScore = score;
            highscoreText.setText("Highscore: " + highScore);
        });

        startButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(startScreen,300);
            fadeOut(overlay, 300);
            fadeIn(gameScreen,300);
            level = 1;
            fadeIn(instructionText, 300);
            rootView.postDelayed(this::startRound, 600);
        });

        submitButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            String input = inputField.getText().toString().trim();
            hideKeyboard(v);
            if (input.equals(currentNumber)) {
                level++;
                feedbackText.setVisibility(View.VISIBLE);
                feedbackText.setText("Correct!");
                handler.postDelayed(this::startRound, 1000);
            } else {
                showGameOver(input);
            }
        });

        numberGameMenuButton.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            enableInputs(false);
            overlay.animate().alpha(1f).setDuration(300).withEndAction(() -> overlay.setVisibility(View.VISIBLE)).start();
            numberGameMenuButton.animate().alpha(0f).setDuration(300).withEndAction(() -> numberGameMenuButton.setVisibility(View.GONE)).start();
            numberGameMenu.setVisibility(View.VISIBLE);
            numberGameMenu.setTranslationY(numberGameMenu.getHeight());
            numberMainScreen.animate().translationY(-80).setDuration(300).start();
            numberGameMenu.animate().translationY(0).setDuration(300).start();

            numberGameMenuBack.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                onBackPressed();
            });

            numberGameMenuPlay.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                enableInputs(true);
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> numberGameMenu.setVisibility(View.GONE)).start();
                numberGameMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> numberGameMenuButton.setVisibility(View.VISIBLE)).start();
                numberMainScreen.animate().translationY(0).setDuration(300).start();
                numberGameMenu.animate().translationY(numberGameMenu.getHeight()).setDuration(300).withEndAction(() -> numberGameMenu.setVisibility(View.GONE)).start();
            });

            numberGameMenuRestart.setOnClickListener(vv -> {
                HapticFeedbackManager.HapticFeedbackLight(vv);
                enableInputs(true);
                overlay.animate().alpha(0f).setDuration(300).withEndAction(() -> numberGameMenu.setVisibility(View.GONE)).start();
                numberGameMenuButton.animate().alpha(1f).setDuration(300).withEndAction(() -> numberGameMenuButton.setVisibility(View.VISIBLE)).start();
                numberMainScreen.animate().translationY(0).setDuration(300).start();
                numberGameMenu.animate().translationY(numberGameMenu.getHeight()).setDuration(300).withEndAction(() -> numberGameMenu.setVisibility(View.GONE)).start();
                Toast.makeText(this, "Restarting...", Toast.LENGTH_SHORT).show();
                level = 1;
                fadeOut(inputField, 100);
                fadeOut(submitButton, 100);
                instructionText.setText("Get ready...");
                fadeIn(instructionText,300);
                rootView.postDelayed(this::startRound, 600);

            });

        });

    }

    @SuppressLint("SetTextI18n")
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

        int displayTime = 1000 + (level * 800);
        timerBar.setVisibility(View.VISIBLE);
        timerBar.setMax(1000);
        timerBar.setProgress(1000);

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

            fadeIn(inputField, 300);
            fadeIn(submitButton, 300);
        }, displayTime);
    }

    @SuppressLint("SetTextI18n")
    private void showGameOver(String userInput) {

        fadeIn(numberGameOverScreen, 300);
        fadeIn(overlay, 300);
        fadeOut(instructionText, 300);
        fadeOut(inputField, 300);
        fadeOut(submitButton, 300);
        feedbackText.setVisibility(View.GONE);
        numberGameOverText.setText("Wrong! Correct was: " + currentNumber);
        numberGameOverScore.setText("You entered: " + userInput + "\nFinal Score: " + (level - 1));

        if (level - 1 > highScore) {
            HighScoreManager.insertHighScore(this, "Number Game", level - 1);
        }

        DailyActivityManager.RecordGame(this, "Number Game");

        numberGameOverBack.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            onBackPressed();
        });
        numberGameOverRetry.setOnClickListener(v->{
            HapticFeedbackManager.HapticFeedbackLight(v);
            fadeOut(numberGameOverScreen,300);
            fadeOut(overlay, 300);
            level = 1;
            instructionText.setText("Get ready...");
            fadeIn(instructionText,300);
            rootView.postDelayed(this::startRound, 600);
        });

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


    private void fadeIn(View view, int duration) {
        view.animate().alpha(1f).setDuration(duration).withEndAction(() -> view.setVisibility(View.VISIBLE)).start();
    }

    private void fadeOut(View view, int duration) {
        view.animate().alpha(0f).setDuration(duration).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }

    private void enableInputs(boolean flag){
        inputField.setEnabled(flag);
        inputField.setClickable(flag);
        inputField.setFocusable(flag);
        submitButton.setEnabled(flag);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
