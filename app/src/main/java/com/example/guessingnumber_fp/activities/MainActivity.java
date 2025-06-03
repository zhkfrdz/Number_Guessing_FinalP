package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ✅ ADDED
import android.os.Bundle;
import com.example.guessingnumber_fp.R;
import android.app.AlertDialog;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;
    // No need for MediaPlayer instances with global SoundManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            startActivityWithTransition(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        isInGameFlow = false;

        // Use #444444 (darker grey) for color change
        int darkGrey = 0xFF444444;
        setupAnimatedButton(R.id.btnPlay, 0xFFFF6B4A, darkGrey, () -> {
            playButtonClickSound();
            isNavigatingWithinApp = true;
            isInGameFlow = true;
            startPlayActivityWithTransition(new Intent(this, SelectDifficultyActivity.class));
        });
        setupAnimatedButton(R.id.btnHighscores, 0xFFFF6B4A, darkGrey, () -> {
            playButtonClickSound();
            isNavigatingWithinApp = true;
            startActivityWithTransition(new Intent(this, HighscoresActivity.class));
        });
        setupAnimatedButton(R.id.btnStats, 0xFFFF6B4A, darkGrey, () -> {
            playButtonClickSound();
            isNavigatingWithinApp = true;
            startActivityWithTransition(new Intent(this, StatsActivity.class));
        });
        setupAnimatedButton(R.id.btnSettings, 0xFFFF6B4A, darkGrey, () -> {
            playButtonClickSound();
            isNavigatingWithinApp = true;
            startActivityWithTransition(new Intent(this, SettingsActivity.class));
        });
        setupAnimatedButton(R.id.btnHelp, 0xFFFF6B4A, darkGrey, () -> {
            playButtonClickSound();
            isNavigatingWithinApp = true;
            startActivityWithTransition(new Intent(this, HelpActivity.class));
        });
        // For Quit button: animate from white to dark grey and back
        setupAnimatedButton(R.id.btnQuit, 0xFFFFFFFF, darkGrey, () -> {
            showQuitDialog();
        });
    }

    private void setupAnimatedButton(int buttonId, int colorFrom, int colorTo, Runnable onClickAction) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
            colorAnimation.setDuration(350);
            colorAnimation.addUpdateListener(animator -> {
                button.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
            });
            colorAnimation.start();
            onClickAction.run();
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
