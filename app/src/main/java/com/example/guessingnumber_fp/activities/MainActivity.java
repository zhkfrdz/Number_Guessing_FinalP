package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ✅ ADDED
import android.os.Bundle;
import com.example.guessingnumber_fp.R;
import android.app.AlertDialog;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;
    private MediaPlayer buttonClickPlayer; // ✅ ADDED

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

        // ✅ Initialize click sound
        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons);

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

    private void playButtonClickSound() { // ✅
        if (buttonClickPlayer != null) {
            buttonClickPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonClickPlayer != null) {
            buttonClickPlayer.release(); // ✅
            buttonClickPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
    }

    private void showQuitDialog() {
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
                    finishAffinity();
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    if (soundOn && isQuitting) {
                        isQuitting = false;
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
