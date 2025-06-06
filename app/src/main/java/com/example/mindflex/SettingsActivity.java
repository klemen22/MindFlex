package com.example.mindflex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerMenuButton;

    private ImageView drawerBackButton;
    Map<String, Integer> musicMap = MusicManager.getMusicMap();


    private final List<String> musicTitles = Arrays.asList("Souls of fire", "At doom's gate", "Into Sandy's city", "The ancient dragon");
    private int musicIndex = 0;
    private ImageView musicArrowLeft;
    private ImageView musicArrowRight;

    private TextView musicTitlePrint;
    private Slider volumeSlider;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch hapticSwitch;

    SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        View rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).top;
            int bottomInset = insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return insets;
        });

        drawerLayout = findViewById(R.id.settings_drawer_layout);
        navigationView = findViewById(R.id.settings_navigation_view);
        drawerMenuButton = findViewById(R.id.settings_drawer_menu_button);
        navigationView.setBackgroundColor(getResources().getColor(R.color.white));
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.settings_text_primary)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.settings_text_primary)));

        drawerMenuButton.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerBackButton = navigationView.findViewById(R.id.drawer_back_button);
                drawerBackButton.setOnClickListener(v -> {
                    HapticFeedbackManager.HapticFeedbackLight(v);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (navigationView != null) {
                    int size = navigationView.getMenu().size();
                    for (int i = 0; i < size; i++) {
                        navigationView.getMenu().getItem(i).setChecked(false);
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.drawer_home) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            if (id == R.id.drawer_about) {
                Toast.makeText(this, "It just works!", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        musicArrowLeft = findViewById(R.id.settings_music_left);
        musicArrowRight = findViewById(R.id.settings_music_right);
        musicTitlePrint = findViewById(R.id.settings_music_title);
        volumeSlider = findViewById(R.id.settings_volume_slider);
        hapticSwitch = findViewById(R.id.settings_haptic_toggle);

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        String savedMusic = preferences.getString("music_key", "Souls of fire");
        musicIndex = musicTitles.indexOf(savedMusic);
        float volumeLevel = preferences.getFloat("volume_key", 1.0f);

        volumeSlider.setValue(volumeLevel * 100f);

        if (musicIndex == -1) {
            musicIndex = 0;
        }

        musicTitlePrint.setText(musicTitles.get(musicIndex));

        musicArrowLeft.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            changeMusic("Left");
        });
        musicArrowRight.setOnClickListener(v -> {
            HapticFeedbackManager.HapticFeedbackLight(v);
            changeMusic("Right");
        });
        volumeSlider.addOnChangeListener((slider, value, fromUser) -> {
            MusicManager.setMusicVolume(value / 100f);
            preferences.edit().putFloat("volume_key", value / 100f).apply();
        });

        boolean hapticEnabled = preferences.getBoolean("haptic_key", true);
        hapticSwitch.setChecked(hapticEnabled);

        hapticSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HapticFeedbackManager.EnableHapticFeedback(isChecked);
            preferences.edit().putBoolean("haptic_key", isChecked).apply();
        });
    }

    private void changeMusic(String direction) {

        if (direction.equals("Left")) {
            musicIndex = (musicIndex - 1 + musicTitles.size()) % musicTitles.size();
        } else if (direction.equals("Right")) {
            musicIndex = (musicIndex + 1) % musicTitles.size();
        }

        String currentMusicTitle = musicTitles.get(musicIndex);
        int currentMusicID = musicMap.get(currentMusicTitle);

        musicTitlePrint.animate().alpha(0f).setDuration(100).withEndAction(() -> {

            musicTitlePrint.setText(currentMusicTitle);
            musicTitlePrint.animate().alpha(1f).setDuration(100).start();
            preferences.edit().putString("music_key", currentMusicTitle).apply();

            MusicManager.startMusic(SettingsActivity.this, currentMusicID);
        }).start();
    }
}