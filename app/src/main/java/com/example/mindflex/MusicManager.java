package com.example.mindflex;

import android.content.Context;
import android.media.MediaPlayer;

import java.net.Inet4Address;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean MusicStopped = false;
    private static int MusicPosition = 0;
    private static float musicVolume = 1.0f;
    private static int currentMusicID = -1; // default value

    public static void startMusic(Context context, int musicID) {
        // first stop music
        stopMusic();

        currentMusicID = musicID;
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), musicID);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(musicVolume, musicVolume);
            mediaPlayer.start();

        }

    }

    public static void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            MusicStopped = false;
        }
    }

    public static void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            MusicPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            MusicStopped = true;
        }
    }

    public static void resumeMusic() {
        if (mediaPlayer != null && MusicStopped) {
            mediaPlayer.seekTo(MusicPosition);
            mediaPlayer.start();
            MusicStopped = false;
        }
    }

    public static int getCurrentMusic() {
        return currentMusicID;
    }

}
