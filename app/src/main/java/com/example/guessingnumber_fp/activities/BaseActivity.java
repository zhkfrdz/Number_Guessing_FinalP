package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class BaseActivity extends AppCompatActivity {
    protected boolean isNavigatingWithinApp = false;
    protected static boolean isInGameFlow = false;
    private static int currentMusicRes = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
    }

    protected void startMenuMusic() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);

        if (!musicOn) {
            MusicManager.stop();
            return;
        }

        isInGameFlow = false;
        if (!MusicManager.isPlaying() || MusicManager.getCurrentMusic() != R.raw.bg_music) {
            MusicManager.start(this, R.raw.bg_music);
        }
    }

    protected void startGameMusic() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);

        if (!musicOn) {
            MusicManager.stop();
            return;
        }

        isInGameFlow = true;
        if (!MusicManager.isPlaying() || MusicManager.getCurrentMusic() != R.raw.bg_music_2) {
            MusicManager.start(this, R.raw.bg_music_2);
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
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
        
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