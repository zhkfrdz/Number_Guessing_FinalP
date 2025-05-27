package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ✅ ADDED
import android.os.Bundle;
import com.example.guessingnumber_fp.R;
import android.app.AlertDialog;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;
    // No need for MediaPlayer instances with global SoundManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        isInGameFlow = false;

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            playButtonClickSound(); // ✅
            isNavigatingWithinApp = true;
            isInGameFlow = true;
            startActivity(new Intent(this, SelectDifficultyActivity.class));
        });

        findViewById(R.id.btnHighscores).setOnClickListener(v -> {
            playButtonClickSound(); // ✅
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, HighscoresActivity.class));
        });

        findViewById(R.id.btnStats).setOnClickListener(v -> {
            playButtonClickSound(); // ✅
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, StatsActivity.class));
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            playButtonClickSound(); // ✅
            isNavigatingWithinApp = true;
            startActivity(new Intent(this, SettingsActivity.class));
        });

        findViewById(R.id.btnQuit).setOnClickListener(v -> {
            // ❌ DO NOT PLAY BUTTON SOUND HERE
            showQuitDialog(); // handled separately
        });
    }

    private void playButtonClickSound() {
        // Use global SoundManager to play button click sound
        SoundManager.playSound(this, R.raw.cat_buttons);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to release MediaPlayer instances with global SoundManager
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
    }

    private void showQuitDialog() {
        // Play quit sound using global SoundManager
        SoundManager.playSound(this, R.raw.quit_st);
        isQuitting = true;

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Quit Game?")
                .setMessage("Are you sure you want to quit?")
                .setIcon(R.drawable.play)
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    MusicManager.release();
                    finishAffinity();
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    // Play back button sound
                    SoundManager.playSound(this, R.raw.cat_back_btn);
                    isQuitting = false;
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
