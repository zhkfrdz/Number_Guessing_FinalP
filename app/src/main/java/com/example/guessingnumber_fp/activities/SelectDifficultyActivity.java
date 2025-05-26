package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.cardview.widget.CardView;
import com.example.guessingnumber_fp.R;

public class SelectDifficultyActivity extends BaseActivity {
    private static final int DIFFICULTY_MUSIC = R.raw.bg_music_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        CardView cardEasy = findViewById(R.id.cardEasy);
        CardView cardMedium = findViewById(R.id.cardMedium);
        CardView cardHard = findViewById(R.id.cardHard);
        ImageButton btnBack = findViewById(R.id.btnBack);

        isInGameFlow = false;

        cardEasy.setOnClickListener(v -> startGame("easy", 10));
        cardMedium.setOnClickListener(v -> startGame("medium", 50));
        cardHard.setOnClickListener(v -> startGame("hard", 100));
        btnBack.setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        isInGameFlow = false;
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
        if (musicOn) {
            MusicManager.setLooping(true);
            MusicManager.start(this, R.raw.bg_music_2);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.stop();
    }

    private void startGame(String difficulty, int max) {
        isNavigatingWithinApp = true;
        MusicManager.pause();
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }
}



