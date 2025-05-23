package com.example.guessingnumber_fp.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static int currentResId = -1;

    public static void start(Context context, int resId) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying() && currentResId == resId) return;
            stop();
        }

        mediaPlayer = MediaPlayer.create(context, resId);
        currentResId = resId;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            currentResId = -1;
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentResId = -1;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    // ✅ New method
    public static boolean isPlaying(int resId) {
        return mediaPlayer != null && mediaPlayer.isPlaying() && currentResId == resId;
    }
}