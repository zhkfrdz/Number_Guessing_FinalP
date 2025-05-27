package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.guessingnumber_fp.R;

public class SelectDifficultyActivity extends BaseActivity {

    private MediaPlayer buttonClickPlayer;
    private MediaPlayer backButtonClickPlayer; // 🎵 ADDED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        LinearLayout cardEasy = findViewById(R.id.cardEasy);
        LinearLayout cardMedium = findViewById(R.id.cardMedium);
        LinearLayout cardHard = findViewById(R.id.cardHard);
        LinearLayout cardImpossible = findViewById(R.id.cardImpossible);
        ImageButton btnBack = findViewById(R.id.btnBack);

        isInGameFlow = true;

        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons);
        backButtonClickPlayer = MediaPlayer.create(this, R.raw.cat_back_btn); // 🎵 Initialize back button sound

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
    }

    private void startGame(String difficulty, int max) {
        isNavigatingWithinApp = true;
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }

    private void playButtonClickSound() {
        if (buttonClickPlayer != null) {
            buttonClickPlayer.start();
        }
    }

    private void playBackButtonClickSound() {
        if (backButtonClickPlayer != null) {
            backButtonClickPlayer.start();
        }
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
}
