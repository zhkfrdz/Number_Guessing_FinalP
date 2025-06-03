package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;

public class BaseActivity extends AppCompatActivity {
    protected boolean isNavigatingWithinApp = false;
    protected static boolean isInGameFlow = false;
    private static int currentMusicRes = -1;
    
    // Database manager for SQLite operations
    protected GameDataManager dataManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
        
        // Enable immersive fullscreen mode to hide system UI including navigation bar
        enableImmersiveMode();
        
        // Initialize database manager
        dataManager = GameDataManager.getInstance(this);
    }

    /**
     * Enables immersive fullscreen mode to hide system UI elements including navigation bar
     * This helps minimize distractions and accidental back button presses
     */
    protected void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        
        // Set a listener to re-enable immersive mode if it gets disabled
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // The system bars are visible, re-hide them
                decorView.setSystemUiVisibility(uiOptions);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Re-enable immersive mode when the window gets focus
            enableImmersiveMode();
        }
    }

    protected void startMenuMusic() {
        // No music in menu screens, always stop music when in menu
        MusicManager.stop();
    }

    protected void startGameMusic() {
        // Use GameDataManager to check music settings
        boolean musicOn = dataManager.isMusicEnabled();

        if (!musicOn) {
            MusicManager.stop();
            return;
        }
        
        // Get current difficulty from preferences
        String difficulty = dataManager.getString("current_difficulty", "easy");
        
        // Play appropriate music based on difficulty
        if (isInGameFlow) {
            switch (difficulty) {
                case "easy":
                    MusicManager.start(this, R.raw.ez_bg_music);
                    break;
                case "medium":
                    MusicManager.start(this, R.raw.medium_bg_music);
                    break;
                case "hard":
                    MusicManager.start(this, R.raw.hardy);
                    break;
                case "impossible":
                    MusicManager.start(this, R.raw.hard_bg_music);
                    break;
                default:
                    MusicManager.start(this, R.raw.ez_bg_music);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp) {
            Log.d("Music", "Pausing music due to activity pause");
            MusicManager.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Use GameDataManager to check music settings
        boolean musicOn = dataManager.isMusicEnabled();
        
        if (!musicOn) {
            MusicManager.stop();
            return;
        }

        // Reset navigation flag first
        isNavigatingWithinApp = false;

        // Then handle music based on game flow
        if (isInGameFlow) {
            Log.d("Music", "Resuming game flow music");
            startGameMusic();
        } else {
            Log.d("Music", "Resuming menu flow music");
            startMenuMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isNavigatingWithinApp) {
            Log.d("Music", "Releasing music due to activity destroy");
            MusicManager.release();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Play the back button sound effect before navigating back
        // This ensures all activities have sound when back button is pressed
        playBackButtonSound();
        
        // Set navigation flags
        isNavigatingWithinApp = true;
        
        // Call the default back button behavior
        super.onBackPressed();
    }
    
    /**
     * Plays the back button sound effect if sound is enabled
     */
    protected void playBackButtonSound() {
        // Check if sound is enabled in settings
        if (dataManager != null && dataManager.isSoundEnabled()) {
            try {
                android.media.MediaPlayer backButtonSound = android.media.MediaPlayer.create(this, R.raw.cat_back_btn);
                if (backButtonSound != null) {
                    backButtonSound.setLooping(false);
                    backButtonSound.setOnCompletionListener(mp -> mp.release());
                    backButtonSound.start();
                }
            } catch (Exception e) {
                Log.e("Sound", "Error playing back button sound", e);
            }
        }
    }
}