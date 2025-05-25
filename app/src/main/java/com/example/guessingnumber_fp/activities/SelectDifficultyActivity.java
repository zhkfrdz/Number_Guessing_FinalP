package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.guessingnumber_fp.R;

public class SelectDifficultyActivity extends BaseActivity {
    private static final int DIFFICULTY_MUSIC = R.raw.bg_music_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        LinearLayout cardEasy = findViewById(R.id.cardEasy);
        LinearLayout cardMedium = findViewById(R.id.cardMedium);
        LinearLayout cardHard = findViewById(R.id.cardHard);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // This is part of the game flow
        isInGameFlow = true;
        startSelectDifficultyMusic();

        cardEasy.setOnClickListener(v -> startGame("easy", 10));
        cardMedium.setOnClickListener(v -> startGame("medium", 50));
        cardHard.setOnClickListener(v -> startGame("hard", 100));
        btnBack.setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            isInGameFlow = false;
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSelectDifficultyMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        isInGameFlow = true;

        // If music isn't playing or it's the wrong track, start it
        if (!MusicManager.isPlaying() || MusicManager.getCurrentMusic() != DIFFICULTY_MUSIC) {
            startSelectDifficultyMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Only pause if we're not navigating within the app
        if (!isNavigatingWithinApp) {
            MusicManager.pause();
        }
    }

    private void startGame(String difficulty, int max) {
        isNavigatingWithinApp = true;
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }

    private void startSelectDifficultyMusic() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
        if (!musicOn) {
            MusicManager.stop();
            return;
        }

        // Only start if not already playing the correct music
        if (!MusicManager.isPlaying() || MusicManager.getCurrentMusic() != DIFFICULTY_MUSIC) {
            MusicManager.setLooping(true);
            MusicManager.start(this, DIFFICULTY_MUSIC);
        }
    }

    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }
}



