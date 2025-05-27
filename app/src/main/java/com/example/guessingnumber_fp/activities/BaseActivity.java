package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class BaseActivity extends AppCompatActivity {
    protected boolean isNavigatingWithinApp = false;
    protected static boolean isInGameFlow = false;
    // Music functionality removed, keeping only sound effects
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
    }

    // Music functionality completely removed
    protected void startMenuMusic() {
        // Music functionality removed
        MusicManager.stop();
    }

    protected void startGameMusic() {
        // Music functionality removed
        MusicManager.stop();
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
        
        // Reset navigation flag first
        isNavigatingWithinApp = false;
        
        // Music functionality removed, only keeping sound effects
        // Stop any music that might be playing
        MusicManager.stop();
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