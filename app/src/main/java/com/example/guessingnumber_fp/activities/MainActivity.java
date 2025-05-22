package com.example.guessingnumber_fp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
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
        setContentView(R.layout.activity_main);

        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        bgMusic.start();

        findViewById(R.id.btnPlay).setOnClickListener(v -> startActivity(new Intent(this, SelectDifficultyActivity.class)));
        findViewById(R.id.btnHighscores).setOnClickListener(v -> startActivity(new Intent(this, HighscoresActivity.class)));
        findViewById(R.id.btnStats).setOnClickListener(v -> startActivity(new Intent(this, StatsActivity.class)));
        findViewById(R.id.btnQuit).setOnClickListener(v -> showQuitDialog());
    }

    private void showQuitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Quit Game?")
                .setMessage("Are you sure you want to quit?")
                .setIcon(R.drawable.play)
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgMusic != null) bgMusic.release();
    }
}