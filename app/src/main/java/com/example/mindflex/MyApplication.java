package com.example.mindflex;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Map;

public class MyApplication extends Application {

    private int activityReference = 0;
    private boolean activityChanging = false;
    SharedPreferences preferences;
    Map<String, Integer> musicMap = MusicManager.getMusicMap();

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        String savedMusic = preferences.getString("music_key", "Souls of fire");
        int musicID = musicMap.get(savedMusic);
        float volumeLevel = preferences.getFloat("volume_key", 1.0f);
        boolean HapticEnabled = preferences.getBoolean("haptic_key", true);


        HapticFeedbackManager.EnableHapticFeedback(HapticEnabled);
        MusicManager.startMusic(this, musicID);
        MusicManager.setMusicVolume(volumeLevel);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (activityReference == 0 && !activityChanging) {
                    MusicManager.resumeMusic();
                }
                activityReference++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityChanging = activity.isChangingConfigurations();
                activityReference--;
                if (activityReference == 0 && !activityChanging) {
                    MusicManager.pauseMusic();
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });

    }
}
