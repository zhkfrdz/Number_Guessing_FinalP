package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

        // Load saved settings
        boolean musicOn = prefs.getBoolean("music_on", true);
        boolean soundOn = prefs.getBoolean("sound_on", true);
        switchMusic.setChecked(musicOn);
        switchSound.setChecked(soundOn);

        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("music_on", isChecked).apply();
                if (isChecked) {
                    startMenuMusic();
                } else {
                    MusicManager.stop();
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
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // Add this for the back button
        android.widget.ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);
        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> {
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                onBackPressed();
            });
        }
    }
    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }
}
