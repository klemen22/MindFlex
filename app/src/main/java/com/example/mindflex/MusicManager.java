package com.example.mindflex;
import android.content.Context;
import android.media.MediaPlayer;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean MusicStopped = false;
    private static int MusicPosition = 0;
    private static final float musicVolume = 1.0f;
    private static int currentMusicID = -1;

    public static void startMusic(Context context, int musicID) {
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

    public static void setMusicVolume(float volumeLevel){
        mediaPlayer.setVolume(volumeLevel, volumeLevel);
    }

    public static Map<String, Integer> getMusicMap(){
        Map<String, Integer> musicMap = new LinkedHashMap<>() {{
            put("Souls of fire", R.raw.souls_of_fire);
            put("At doom's gate", R.raw.at_dooms_gate);
            put("Into Sandy's city", R.raw.into_sandys_city);
            put("The ancient dragon", R.raw.the_ancient_dragon);
        }};

        return musicMap;
    }

}
