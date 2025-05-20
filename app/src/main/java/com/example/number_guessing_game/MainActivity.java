package com.example.number_guessing_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView highScoresText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("GameScores", MODE_PRIVATE);
        highScoresText = findViewById(R.id.highScoresText);

        Button easyButton = findViewById(R.id.easyButton);
        Button mediumButton = findViewById(R.id.mediumButton);
        Button hardButton = findViewById(R.id.hardButton);
        Button impossibleButton = findViewById(R.id.impossibleButton);

        easyButton.setOnClickListener(v -> startGame("Easy", 10));
        mediumButton.setOnClickListener(v -> startGame("Medium", 50));
        hardButton.setOnClickListener(v -> startGame("Hard", 100));
        impossibleButton.setOnClickListener(v -> startGame("Impossible", 500));

        updateHighScores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHighScores();
    }

    private void startGame(String difficulty, int maxNumber) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("DIFFICULTY", difficulty);
        intent.putExtra("MAX_NUMBER", maxNumber);
        startActivity(intent);
    }

    private void updateHighScores() {
        int easyScore = sharedPreferences.getInt("Easy", 0);
        int mediumScore = sharedPreferences.getInt("Medium", 0);
        int hardScore = sharedPreferences.getInt("Hard", 0);
        int impossibleScore = sharedPreferences.getInt("Impossible", 0);

        String scoresText = String.format("Easy: %d\nMedium: %d\nHard: %d\nImpossible: %d",
                easyScore, mediumScore, hardScore, impossibleScore);
        highScoresText.setText(scoresText);
    }
} 