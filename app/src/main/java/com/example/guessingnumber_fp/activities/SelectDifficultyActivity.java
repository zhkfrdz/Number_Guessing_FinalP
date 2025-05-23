package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.utils.MusicManager;

public class SelectDifficultyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
        setContentView(R.layout.activity_select_difficulty);

        LinearLayout cardEasy = findViewById(R.id.cardEasy);
        LinearLayout cardMedium = findViewById(R.id.cardMedium);
        LinearLayout cardHard = findViewById(R.id.cardHard);
        ImageButton btnBack = findViewById(R.id.btnBack);

        cardEasy.setOnClickListener(v -> startGame("easy", 10));
        cardMedium.setOnClickListener(v -> startGame("medium", 50));
        cardHard.setOnClickListener(v -> startGame("hard", 100));
        btnBack.setOnClickListener(v -> finish());
    }

    private void startGame(String difficulty, int max) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only start the music if it's not already playing bg_music_2
        if (!MusicManager.isPlaying(R.raw.bg_music_2)) {
            MusicManager.start(this, R.raw.bg_music_2);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        // Do NOT pause or stop here to allow continuous playback
    }


}