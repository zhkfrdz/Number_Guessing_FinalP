package com.example.guessingnumber_fp.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import com.example.guessingnumber_fp.database.GameDataManager;

/**
 * Professional singleton music manager for background music in Android apps.
 * Handles play, pause, resume, stop, and release, and avoids memory leaks.
 */
public class MusicManager {
    private static final String TAG = "MusicManager";
    private static MediaPlayer mediaPlayer = null;
    private static int currentResId = -1;
    private static boolean isPaused = false;
    private static boolean shouldLoop = true;
    private static Context appContext = null;

    private MusicManager() { /* Prevent instantiation */ }

    /**
     * Start playing the given music resource. If already playing the same, does nothing.
     * @param context Any context (activity or app context)
     * @param resId Resource ID of the music to play
     */
    public static synchronized void start(Context context, int resId) {
        try {
            appContext = context.getApplicationContext();
            
            // Check if music is enabled in GameDataManager
            GameDataManager dataManager = GameDataManager.getInstance(context);
            if (!dataManager.isMusicEnabled()) {
                // If music is disabled, stop any playing music and return
                stop();
                return;
            }
            
            if (mediaPlayer != null && currentResId == resId && mediaPlayer.isPlaying()) {
                // Already playing the requested music
                return;
            }
            // Clean up previous player if needed
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping previous player: " + e.getMessage());
                }
                try {
                    mediaPlayer.release();
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing previous player: " + e.getMessage());
                }
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(appContext, resId);
            if (mediaPlayer != null) {
                currentResId = resId;
                mediaPlayer.setLooping(shouldLoop);
                mediaPlayer.start();
                isPaused = false;
                Log.d(TAG, "Started playing music: " + resId);
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for resId: " + resId);
                currentResId = -1;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in start(): " + e.getMessage());
            release();
        }
    }

    /**
     * Pause the music if playing.
     */
    public static synchronized void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPaused = true;
                Log.d(TAG, "Paused music: " + currentResId);
            } catch (Exception e) {
                Log.e(TAG, "Error in pause(): " + e.getMessage());
            }
        }
    }

    /**
     * Resume the music if paused.
     */
    public static synchronized void resume() {
        if (mediaPlayer != null && isPaused) {
            try {
                // Check if music is enabled in GameDataManager
                if (appContext != null) {
                    GameDataManager dataManager = GameDataManager.getInstance(appContext);
                    if (!dataManager.isMusicEnabled()) {
                        // If music is disabled, don't resume
                        return;
                    }
                }
                
                mediaPlayer.start();
                isPaused = false;
                Log.d(TAG, "Resumed music: " + currentResId);
            } catch (Exception e) {
                Log.e(TAG, "Error in resume(): " + e.getMessage());
                // Try to restart if resume fails
                if (appContext != null && currentResId != -1) {
                    start(appContext, currentResId);
                }
            }
        }
    }

    /**
     * Stop and release the music player.
     */
    public static synchronized void stop() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in stop(): " + e.getMessage());
            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error in release(): " + e.getMessage());
            }
            mediaPlayer = null;
            currentResId = -1;
            isPaused = false;
            Log.d(TAG, "Stopped and released music player");
        }
    }

    /**
     * Release all resources and clear context reference.
     */
    public static synchronized void release() {
        stop();
        appContext = null;
        Log.d(TAG, "MusicManager released");
    }

    /**
     * Set whether music should loop.
     * @param loop true to loop, false otherwise
     */
    public static synchronized void setLooping(boolean loop) {
        shouldLoop = loop;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setLooping(loop);
            } catch (Exception e) {
                Log.e(TAG, "Error in setLooping(): " + e.getMessage());
            }
        }
    }

    /**
     * @return true if music is currently playing
     */
    public static synchronized boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e) {
            Log.e(TAG, "Error in isPlaying(): " + e.getMessage());
            return false;
        }
    }

    /**
     * @return the resource ID of the currently playing music, or -1 if none
     */
    public static synchronized int getCurrentMusic() {
        return currentResId;
    }

    /**
     * @return true if music is paused
     */
    public static synchronized boolean isPaused() {
        return isPaused;
    }

    /**
     * Switch to a new music resource, stopping the current one if needed.
     * @param context Any context
     * @param newResId Resource ID of the new music
     */
    public static synchronized void switchMusic(Context context, int newResId) {
        if (currentResId != newResId) {
            start(context, newResId);
        }
    }

    // Optionally: add volume control, fade in/out, etc. as needed for your app
}
