package com.example.number_guessing_game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private int targetNumber;
    private int lives;
    private int maxNumber;
    private String difficulty;
    private TextView livesTextView;
    private TextView difficultyTextView;
    private TextView feedbackTextView;
    private EditText guessEditText;
    private Button submitButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize views
        livesTextView = findViewById(R.id.livesTextView);
        difficultyTextView = findViewById(R.id.difficultyTextView);
        feedbackTextView = findViewById(R.id.feedbackTextView);
        guessEditText = findViewById(R.id.guessEditText);
        submitButton = findViewById(R.id.submitButton);

        // Get difficulty and max number from intent
        difficulty = getIntent().getStringExtra("DIFFICULTY");
        maxNumber = getIntent().getIntExtra("MAX_NUMBER", 10);

        // Initialize game
        lives = 3;
        Random random = new Random();
        targetNumber = random.nextInt(maxNumber) + 1;

        // Update UI
        difficultyTextView.setText("Difficulty: " + difficulty);
        livesTextView.setText("Lives: " + lives);
        sharedPreferences = getSharedPreferences("GameScores", MODE_PRIVATE);

        submitButton.setOnClickListener(v -> checkGuess());
    }

    private void checkGuess() {
        String guessStr = guessEditText.getText().toString();
        if (guessStr.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int guess = Integer.parseInt(guessStr);
        guessEditText.setText("");

        if (guess < 1 || guess > maxNumber) {
            Toast.makeText(this, "Please enter a number between 1 and " + maxNumber, Toast.LENGTH_SHORT).show();
            return;
        }

        if (guess == targetNumber) {
            // Player won
            int currentHighScore = sharedPreferences.getInt(difficulty, 0);
            if (lives > currentHighScore) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(difficulty, lives);
                editor.apply();
            }
            feedbackTextView.setText("Congratulations! You won with " + lives + " lives remaining!");
            submitButton.setEnabled(false);
            guessEditText.setEnabled(false);
        } else {
            lives--;
            livesTextView.setText("Lives: " + lives);

            if (lives <= 0) {
                feedbackTextView.setText("Game Over! The number was " + targetNumber);
                submitButton.setEnabled(false);
                guessEditText.setEnabled(false);
            } else {
            } else {
                String hint = guess < targetNumber ? "Too low!" : "Too high!";
                feedbackTextView.setText(hint);
            }
        }
    }
} 