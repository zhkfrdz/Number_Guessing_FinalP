package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class SettingsActivity extends BaseActivity {
    private Switch switchMusic, switchSound;
    private Button btnLogout;
    private SharedPreferences prefs;
    private MediaPlayer buttonClickPlayer;
    private MediaPlayer backButtonClickPlayer; // 🎵 ADDED for back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        switchMusic = findViewById(R.id.switchMusic);
        switchSound = findViewById(R.id.switchSound);
        btnLogout = findViewById(R.id.btnLogout);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons);
        backButtonClickPlayer = MediaPlayer.create(this, R.raw.cat_back_btn); // 🎵 INIT

        boolean musicOn = prefs.getBoolean("music_on", true);
        boolean soundOn = prefs.getBoolean("sound_on", true);
        switchMusic.setChecked(musicOn);
        switchSound.setChecked(soundOn);

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("music_on", isChecked).apply();
            if (isChecked) {
                startMenuMusic();
            } else {
                MusicManager.stop();
            }
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_on", isChecked).apply();
        });

        btnLogout.setOnClickListener(v -> {
            playButtonClickSound(); // 🔊 Standard click sound
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });

        ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);
        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> {
                playBackButtonClickSound(); // 🔊 Back button sound
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                onBackPressed();
            });
        }
    }

    private void playButtonClickSound() {
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && buttonClickPlayer != null) {
            buttonClickPlayer.start();
        }
    }

    private void playBackButtonClickSound() { // 🎵 NEW METHOD
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && backButtonClickPlayer != null) {
            backButtonClickPlayer.start();
        }
    }

    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonClickPlayer != null) {
            buttonClickPlayer.release();
            buttonClickPlayer = null;
        }
        if (backButtonClickPlayer != null) {
            backButtonClickPlayer.release();
            backButtonClickPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
    }
}
