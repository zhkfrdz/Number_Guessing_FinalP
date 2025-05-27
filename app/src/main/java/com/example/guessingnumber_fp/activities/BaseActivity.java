package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
        
        // Initialize database manager
        dataManager = GameDataManager.getInstance(this);
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
} 