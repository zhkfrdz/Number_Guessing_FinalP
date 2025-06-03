package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.guessingnumber_fp.R;

public class SelectDifficultyActivity extends BaseActivity {

    // No need for MediaPlayer instances with global SoundManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        LinearLayout cardEasy = findViewById(R.id.cardEasy);
        LinearLayout cardMedium = findViewById(R.id.cardMedium);
        LinearLayout cardHard = findViewById(R.id.cardHard);
        LinearLayout cardImpossible = findViewById(R.id.cardImpossible);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Explicitly set to false to ensure no music plays in this activity
        isInGameFlow = false;
        
        // Explicitly stop any music that might be playing
        MusicManager.stop();

        cardEasy.setOnClickListener(v -> {
            playButtonClickSound();
            startGame("easy", 10);
        });

        cardMedium.setOnClickListener(v -> {
            playButtonClickSound();
            startGame("medium", 30);
        });

        cardHard.setOnClickListener(v -> {
            playButtonClickSound();
            startGame("hard", 50);
        });
        
        cardImpossible.setOnClickListener(v -> {
            playButtonClickSound();
            startGame("impossible", 100);
        });

        btnBack.setOnClickListener(v -> {
            playBackButtonClickSound(); // 🔊 Play back button sound
            isNavigatingWithinApp = true;
            isInGameFlow = false;
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // BaseActivity will handle music control
    }

    private void startGame(String difficulty, int max) {
        isNavigatingWithinApp = true;
        isInGameFlow = true;
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startPlayActivityWithTransition(intent);
    }

    private void playButtonClickSound() {
        // Use global SoundManager to play button click sound
        SoundManager.playSound(this, R.raw.cat_buttons);
    }

    private void playBackButtonClickSound() {
        // Use global SoundManager to play back button sound
        SoundManager.playSound(this, R.raw.cat_back_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to release MediaPlayer instances with global SoundManager
    }
}
