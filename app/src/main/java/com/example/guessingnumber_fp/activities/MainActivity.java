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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.guessingnumber_fp.fragments.MainMenuFragment;
import com.example.guessingnumber_fp.fragments.SelectDifficultyFragment;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;
    // No need for MediaPlayer instances with global SoundManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MainMenuFragment())
                .commit();
        }

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        isInGameFlow = false;
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

    public void showQuitDialog() {
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

    public void showSelectDifficultyFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container, new SelectDifficultyFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMainMenuFragment() {
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, new MainMenuFragment())
            .commit();
    }
}
