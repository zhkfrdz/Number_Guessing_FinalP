package com.example.guessingnumber_fp.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static int currentResId = -1;
    private static boolean shouldLoop = true;
    private static boolean isPaused = false;
    private static Context appContext;

    public static void start(Context context, int resId) {
        try {
            appContext = context.getApplicationContext();

            // If the same music is already playing, don't restart it
            if (mediaPlayer != null && currentResId == resId && mediaPlayer.isPlaying()) {
                return;
            }

            // If we have a previous player, clean it up properly
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                } catch (Exception e) {
                    Log.e("Music", "Error cleaning up previous player: " + e.getMessage());
                }
            }

            // Create and start new player
            mediaPlayer = MediaPlayer.create(appContext, resId);
            if (mediaPlayer != null) {
                currentResId = resId;
                mediaPlayer.setLooping(shouldLoop);
                mediaPlayer.start();
                isPaused = false;
                Log.d("Music", "Started playing music: " + resId);
            }
        } catch (Exception e) {
            Log.e("Music", "Error in start(): " + e.getMessage());
            release();
        }
    }

    public static void setLooping(boolean loop) {
        shouldLoop = loop;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setLooping(loop);
            } catch (Exception e) {
                Log.e("Music", "Error in setLooping(): " + e.getMessage());
            }
        }
    }

    public static void pause() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    Log.d("Music", "Pausing music: " + currentResId);
                    mediaPlayer.pause();
                    isPaused = true;
                }
            } catch (Exception e) {
                Log.e("Music", "Error in pause(): " + e.getMessage());
                release();
            }
        }
    }

    public static void resume() {
        if (mediaPlayer != null && isPaused) {
            try {
                Log.d("Music", "Resuming music: " + currentResId);
                mediaPlayer.start();
                isPaused = false;
            } catch (Exception e) {
                Log.e("Music", "Error in resume(): " + e.getMessage());
                // If resume fails, try to restart the music
                if (appContext != null && currentResId != -1) {
                    start(appContext, currentResId);
                }
            }
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            try {
                Log.d("Music", "Stopping music: " + currentResId);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("Music", "Error in stop(): " + e.getMessage());
            } finally {
                mediaPlayer = null;
                currentResId = -1;
                isPaused = false;
            }
        }
    }

    public static void release() {
        Log.d("Music", "Releasing music resources: " + currentResId);
        stop();
        appContext = null;
    }

    public static boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e) {
            Log.e("Music", "Error in isPlaying(): " + e.getMessage());
            return false;
        }
    }

    public static int getCurrentMusic() {
        return currentResId;
    }

    public static boolean isPaused() {
        return isPaused;
    }

    public static void switchMusic(Context context, int newResId) {
        Log.d("Music", "Switching music from " + currentResId + " to " + newResId);
        stop();
        start(context, newResId);
    }
}