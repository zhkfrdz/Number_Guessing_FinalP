package com.example.guessingnumber_fp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class MainActivity extends AppCompatActivity {
    MediaPlayer bgMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            // No user logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        boolean musicOn = prefs.getBoolean("music_on", true);
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        if (musicOn) {
            bgMusic.start();
        }

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
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

    private void showQuitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Quit Game?")
                .setMessage("Are you sure you want to quit?")
                .setIcon(R.drawable.play)
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    finishAffinity(); // Closes all activities and exits the app
                })
                .setNegativeButton("No", null)
                .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFF6B4A);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFF6B4A);
        });
        
        dialog.show();
    }

    private boolean isNavigatingWithinApp = false;

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp && bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
        if (bgMusic != null && musicOn) {
            bgMusic.start();
        } else if (bgMusic != null && !musicOn) {
            bgMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgMusic != null) bgMusic.release();
    }
}