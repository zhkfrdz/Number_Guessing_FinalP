package com.example.guessingnumber_fp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import com.example.guessingnumber_fp.R;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            // No user logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        // Ensure we're in menu flow
        isInGameFlow = false;
        startMenuMusic();

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            isInGameFlow = true; // Set game flow before starting SelectDifficultyActivity
            startActivity(new Intent(this, SelectDifficultyActivity.class));
        });
        findViewById(R.id.btnHighscores).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, HighscoresActivity.class));
        });
        findViewById(R.id.btnStats).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, StatsActivity.class));
        });
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, SettingsActivity.class));
        });
        findViewById(R.id.btnQuit).setOnClickListener(v -> {
            showQuitDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        
        // If not quitting, ensure proper music is playing
        if (!isQuitting) {
            SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
            boolean musicOn = prefs.getBoolean("music_on", true);
            if (musicOn) {
                // Always reset to menu flow when returning to main menu
                isInGameFlow = false;
                startMenuMusic();
            }
        }
    }

    private void showQuitDialog() {
        // Play quit sound if sound is enabled
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn) {
            isQuitting = true;
            MusicManager.setLooping(false);
            MusicManager.start(this, R.raw.quit_st);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Quit Game?")
                .setMessage("Are you sure you want to quit?")
                .setIcon(R.drawable.play)
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    MusicManager.release();
                    finishAffinity(); // Closes all activities and exits the app
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    if (soundOn && isQuitting) {
                        // Restore background music if not quitting
                        isQuitting = false;
                        startMenuMusic();
                    }
                })
                .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFF6B4A);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFF6B4A);
        });
        
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp && !isQuitting) {
            MusicManager.pause();
        }
    }
}