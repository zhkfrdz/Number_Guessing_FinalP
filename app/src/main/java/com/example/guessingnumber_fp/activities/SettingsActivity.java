package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Button;
import com.example.guessingnumber_fp.R;

public class SettingsActivity extends BaseActivity {
    private Switch switchMusic, switchSound;
    private Button btnLogout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchMusic = findViewById(R.id.switchMusic);
        switchSound = findViewById(R.id.switchSound);
        btnLogout = findViewById(R.id.btnLogout);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        // Load saved settings
        boolean musicOn = prefs.getBoolean("music_on", true);
        boolean soundOn = prefs.getBoolean("sound_on", true);
        switchMusic.setChecked(musicOn);
        switchSound.setChecked(soundOn);

        startMenuMusic();

        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("music_on", isChecked).apply();
                if (isChecked) {
                    if (isInGameFlow) {
                        startGameMusic();
                    } else {
                        startMenuMusic();
                    }
                } else {
                    MusicManager.pause();
                }
            }
        });
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("sound_on", isChecked).apply();
            }
        });
        btnLogout.setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
