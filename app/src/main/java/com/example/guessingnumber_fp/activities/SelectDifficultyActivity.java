package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ADDED
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.activities.MusicManager;

public class SelectDifficultyActivity extends BaseActivity {
    // private static final int DIFFICULTY_MUSIC = R.raw.bg_music_2;

    private MediaPlayer buttonClickPlayer; // ADDED

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
        // startSelectDifficultyMusic();

        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons); // ADDED

        cardEasy.setOnClickListener(v -> {
            playButtonClickSound(); // ADDED
            startGame("easy", 10);
        });

        cardMedium.setOnClickListener(v -> {
            playButtonClickSound(); // ADDED
            startGame("medium", 50);
        });

        cardHard.setOnClickListener(v -> {
            playButtonClickSound(); // ADDED
            startGame("hard", 100);
        });

        btnBack.setOnClickListener(v -> {
            playButtonClickSound(); // ADDED
            isNavigatingWithinApp = true;
            isInGameFlow = false;
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove background music logic from SelectDifficultyActivity
        // No MusicManager.start() here
    }

    private void startGame(String difficulty, int max) {
        isNavigatingWithinApp = true;
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }

    private void playButtonClickSound() { // ADDED
        if (buttonClickPlayer != null) {
            buttonClickPlayer.start();
        }
    }

    @Override
    protected void onDestroy() { // ADDED
        super.onDestroy();
        if (buttonClickPlayer != null) {
            buttonClickPlayer.release();
            buttonClickPlayer = null;
        }
    }

    // private void startSelectDifficultyMusic() {
    //     SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
    //     boolean musicOn = prefs.getBoolean("music_on", true);
    //     if (!musicOn) {
    //         MusicManager.stop();
    //         return;
    //     }

    //     // Only start if not already playing the correct music
    //     if (!MusicManager.isPlaying() || MusicManager.getCurrentMusic() != DIFFICULTY_MUSIC) {
    //         MusicManager.setLooping(true);
    //         MusicManager.start(this, DIFFICULTY_MUSIC);
    //     }
    // }
}
