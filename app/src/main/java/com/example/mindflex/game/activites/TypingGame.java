package com.example.mindflex.game.activites;
import static android.view.View.GONE;
import static android.widget.Toast.*;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mindflex.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class TypingGame extends AppCompatActivity {

    private Button backButton;
    private View typeOverlay;
    private Button typeStartButton;
    private LinearLayout typeStartScreen;
    private TextView typeTextBox;
    private EditText typeInput;
    private TextView typeTime;
    private TextView typeWPM;
    private TextView typeRound;

    private ArrayList<String> sentences;
    private String currentSentence;
    private boolean testFlag = false;
    private long startTime = 0;
    private boolean timerRunning = false;
    private android.os.Handler timerHandler = new android.os.Handler();
    private Runnable timerRunnable;
    private int wpm = 0;
    private int wordCount = 0;
    private int roundNum = 0;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_typing_game);

        // fix screen space
        View rootView = findViewById(android.R.id.content);

        // for now bottom and top part of the screen space will be limited
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        // xml elements initialization \\
        backButton = findViewById(R.id.type_game_back);
        typeOverlay = findViewById(R.id.type_overlay);
        typeRound = findViewById(R.id.type_game_round);
        // xml elements start screen
        typeStartButton = findViewById(R.id.type_start_button);
        typeStartScreen = findViewById(R.id.type_game_start_screen);
        // xml elements main screen
        typeTextBox = findViewById(R.id.type_game_text);
        typeInput = findViewById(R.id.type_game_input);
        typeTime = findViewById(R.id.type_game_time);
        typeWPM = findViewById(R.id.type_game_wpm);

        // load sentences
        sentences = loadSentences("sentences1.txt");

        // back buttom
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // disable suggestions and auto correct (hack)
        typeInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);


        typeStartButton.setOnClickListener(v -> {
            typeStartScreen.setVisibility(GONE);
            typeOverlay.setVisibility(GONE);
            startRound();
        });

        typeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!testFlag) {
                    return;
                }

                if(!timerRunning && s.length() > 0) {
                    startTime = System.currentTimeMillis();
                    timerRunning = true;
                    timerHandler.postDelayed(timerRunnable, 0);
                }

                typingStats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timerRunning) {
                    long millis = System.currentTimeMillis() - startTime;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    typeTime.setText(String.format("Time: %02d:%02d", minutes, seconds));
                    wordCount = typeInput.getText().toString().split("\\s+").length;
                    wpm = (int) (wordCount / (millis / 60000.0));
                    if (millis > 2000) {
                        typeWPM.setText(String.format("WPM: %d", wpm));
                    } else {
                        typeWPM.setText("WPM: 0");
                    }
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startRound() {
        if (sentences.isEmpty()) {
            Toast.makeText(this, "Sentences are not aviable", LENGTH_SHORT).show();
            return;
        }
        roundNum++;
        typeRound.setText("Round: " + roundNum);
        fadeText(typeInput, "");
        typeWPM.setText("WPM: 0");
        typeTime.setText("Time: 00:00");
        int randNum = new Random().nextInt(sentences.size());
        currentSentence = sentences.get(randNum);
        fadeText(typeTextBox, currentSentence);
        testFlag = true;
    }

    private void typingStats(String userInput) {
        View rootView = findViewById(android.R.id.content);
        Spannable spannable = new SpannableString(currentSentence);

        int length = Math.min(userInput.length(), currentSentence.length());

        for (int i = 0; i < currentSentence.length(); i++) {
            if (i < length) {
                if (userInput.charAt(i) == currentSentence.charAt(i)) {
                    spannable.setSpan(
                            new BackgroundColorSpan(Color.parseColor("#66BB6A")),
                            i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                } else {
                    spannable.setSpan(
                            new BackgroundColorSpan(Color.parseColor("#E57373")),
                            i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        }
        typeTextBox.setText(spannable);

        if (userInput.length() == currentSentence.length()) {
            testFlag = false;
            timerRunning = false;
            Toast.makeText(this, "Starting next round", LENGTH_SHORT).show();
            rootView.postDelayed(this::startRound, 500);
        }
    }


    @NonNull
    private ArrayList<String> loadSentences(String fileName) {
        ArrayList<String> sentencesList = new ArrayList<>();
        try {
            InputStream stream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    sentencesList.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error while loading assets!", LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return sentencesList;
    }


    private void fadeText (TextView textView, String text) {
        textView.animate().alpha(0f).setDuration(200).withEndAction(()->{
            textView.setText(text);
            textView.animate().alpha(1f).setDuration(200).start();
        }).start();
    }

}