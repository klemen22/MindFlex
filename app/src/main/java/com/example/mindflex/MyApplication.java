package com.example.mindflex;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends Application {

    private int activityReference = 0;
    private boolean activityChanging = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: fix music reading and settings at the start of the app
        MusicManager.startMusic(this, R.raw.souls_of_fire);

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
                if (activityReference == 0 && !activityChanging){
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
