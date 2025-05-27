package com.example.guessingnumber_fp.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import com.example.guessingnumber_fp.database.GameDataManager;

/**
 * Professional singleton sound manager for sound effects in Android apps.
 * Handles playing sound effects with respect to global sound settings.
 */
public class SoundManager {
    private static final String TAG = "SoundManager";
    
    private SoundManager() { /* Prevent instantiation */ }
    
    /**
     * Play a sound effect if sound is enabled in settings
     * @param context Any context (activity or app context)
     * @param resId Resource ID of the sound to play
     */
    public static void playSound(Context context, int resId) {
        try {
            // Check if sound is enabled in GameDataManager
            GameDataManager dataManager = GameDataManager.getInstance(context);
            if (!dataManager.isSoundEnabled()) {
                // If sound is disabled, don't play anything
                return;
            }
            
            // Create and play a one-time sound effect
            MediaPlayer player = MediaPlayer.create(context, resId);
            if (player != null) {
                player.setOnCompletionListener(mp -> {
                    mp.release();
                });
                player.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing sound: " + e.getMessage());
        }
    }
}
