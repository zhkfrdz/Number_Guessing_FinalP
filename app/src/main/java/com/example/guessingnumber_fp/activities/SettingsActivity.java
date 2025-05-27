package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ✅ ADDED
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class SettingsActivity extends BaseActivity {
    private Switch switchMusic, switchSound;
    private Button btnLogout;
    private SharedPreferences prefs;
    private MediaPlayer buttonClickPlayer; // ✅ ADDED

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

        // ✅ Initialize click sound
        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons);

        // Load saved settings
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
            playButtonClickSound(); // ✅ Play sound on logout
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // Back button functionality
        android.widget.ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);
        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> {
                playButtonClickSound(); // ✅ Play sound on back
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                onBackPressed();
            });
        }
    }

    private void playButtonClickSound() { // ✅ ADDED
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && buttonClickPlayer != null) {
            buttonClickPlayer.start();
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
            buttonClickPlayer.release(); // ✅ Clean up MediaPlayer
            buttonClickPlayer = null;
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

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }
}
