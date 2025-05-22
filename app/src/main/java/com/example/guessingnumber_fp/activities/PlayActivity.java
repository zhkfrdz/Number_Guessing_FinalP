package com.example.guessingnumber_fp.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import com.example.guessingnumber_fp.R;

public class PlayActivity extends AppCompatActivity {
    int targetNumber, score = 0, hearts = 3, hints = 3, difficultyMax = 50;
    int gamesPlayed = 0, gamesWon = 0, gamesLost = 0, hintsUsed = 0;
    String difficulty = "easy";
    Random random = new Random();

    // Mocking messages arrays
    private final String[] tooLowMessages = {
        "Wow, aiming that low? Are you even trying?",
        "That's cute. Add some digits, maybe you'll get close.",
        "This isn't limbo, you don't have to go that low.",
        "Did you guess with your eyes closed?",
        "Colder than your last relationship. Try higher."
    };

    private final String[] tooHighMessages = {
        "Relax, it's not your ego. Go lower.",
        "That number is as inflated as your confidence.",
        "Trying to touch the sun, are we? Bring it down.",
        "You overshot it like your life goals."
    };

    TextView tvLevel, tvRange, tvHint, tvHintMessage;
    EditText etGuess;
    ImageView heart1, heart2, heart3;
    Button btnGuess, btnGiveUp, btnHint;
    Handler handler = new Handler();

    SharedPreferences prefs;

    private String originalRangeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
        setContentView(R.layout.activity_play);

        tvLevel = findViewById(R.id.tvLevel);
        tvRange = findViewById(R.id.tvRange);
        tvHint = findViewById(R.id.tvHint);
        tvHintMessage = findViewById(R.id.tvHintMessage);
        etGuess = findViewById(R.id.etGuess);
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);
        btnGuess = findViewById(R.id.btnGuess);
        btnGiveUp = findViewById(R.id.btnGiveUp);
        btnHint = findViewById(R.id.btnHintButton);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        // Get difficulty and range from Intent
        difficulty = getIntent().getStringExtra("difficulty");
        difficultyMax = getIntent().getIntExtra("max", 10);
        String levelText = "EASY";
        if ("medium".equals(difficulty)) levelText = "MEDIUM";
        else if ("hard".equals(difficulty)) levelText = "HARD";
        tvLevel.setText(levelText + " LEVEL");
        originalRangeMessage = "We are thinking of a number between 1 and " + difficultyMax;
        tvRange.setText(originalRangeMessage);
        startNewGame();

        btnGuess.setOnClickListener(v -> checkGuess());
        btnGiveUp.setOnClickListener(v -> giveUp());
        btnHint.setOnClickListener(v -> useHint());
    }

    private void startNewGame() {
        targetNumber = random.nextInt(difficultyMax) + 1;
        score = 0;
        hearts = 3;
        hints = 3; // Set default hints to 3
        updateHearts();
        tvHint.setText("HINTS: " + hints);
        etGuess.setText("");
        gamesPlayed++;
    }

    private void useHint() {
        if (hints > 0) {
            hints--;
            hintsUsed++;
            prefs.edit().putInt("hints_used_" + difficulty, hintsUsed).apply();
            tvHint.setText("HINTS: " + hints);

            // Get a random hint message
            String hintMessage;
            if (random.nextBoolean()) {
                // Give a range hint
                int range = difficultyMax / 4;
                int lowerBound = (targetNumber / range) * range + 1;
                int upperBound = lowerBound + range - 1;
                hintMessage = "The number is between " + lowerBound + " and " + upperBound;
            } else {
                // Give a divisibility hint
                if (targetNumber % 2 == 0) {
                    hintMessage = "The number is even";
                } else {
                    hintMessage = "The number is odd";
                }
            }
            
            // Show hint message in custom TextView
            tvHintMessage.setText(hintMessage);
            tvHintMessage.setVisibility(View.VISIBLE);
            
            // Hide the message after 3 seconds
            handler.postDelayed(() -> {
                tvHintMessage.setVisibility(View.GONE);
            }, 3000);
        } else {
            tvHintMessage.setText("No hints remaining!");
            tvHintMessage.setVisibility(View.VISIBLE);
            
            // Hide the message after 2 seconds
            handler.postDelayed(() -> {
                tvHintMessage.setVisibility(View.GONE);
            }, 2000);
        }
    }

    private String getMockingMessage(int guess) {
        if (guess < targetNumber) {
            return tooLowMessages[random.nextInt(tooLowMessages.length)];
        } else {
            return tooHighMessages[random.nextInt(tooHighMessages.length)];
        }
    }

    private void checkGuess() {
        String guessStr = etGuess.getText().toString();
        if (guessStr.isEmpty()) return;
        int guess = Integer.parseInt(guessStr);
        if (guess == targetNumber) {
            score++;
            hearts = 3;
            updateHearts();
            if (score % 3 == 0) {
                hints++;
                prefs.edit().putInt("hints_" + difficulty, hints).apply();
                Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
            }
            tvHint.setText("HINTS: " + hints);
            // Save highscore if needed
            int highscore = prefs.getInt("highscore_" + difficulty, 0);
            if (score > highscore) {
                prefs.edit().putInt("highscore_" + difficulty, score).apply();
            }
            // Restore original range message
            tvRange.setText(originalRangeMessage);
            // Animation or toast
            Toast.makeText(this, "Correct! New number.", Toast.LENGTH_SHORT).show();
            targetNumber = random.nextInt(difficultyMax) + 1;
            etGuess.setText("");
        } else {
            hearts--;
            updateHearts();
            if (hearts == 0) {
                gamesLost++;
                prefs.edit().putInt("games_lost_" + difficulty, gamesLost).apply();
                new AlertDialog.Builder(this)
                        .setTitle("Game Over")
                        .setMessage("You lost! Score: " + score)
                        .setPositiveButton("OK", (d, w) -> finish())
                        .show();
            } else {
                // Show mocking message in tvRange
                tvRange.setText(getMockingMessage(guess));
            }
        }
    }

    private void giveUp() {
        gamesLost++;
        prefs.edit().putInt("games_lost_" + difficulty, gamesLost).apply();
        new AlertDialog.Builder(this)
                .setTitle("Gave Up")
                .setMessage("The number was: " + targetNumber)
                .setPositiveButton("OK", (d, w) -> finish())
                .show();
    }

    private void updateHearts() {
        heart1.setImageResource(hearts >= 1 ? R.drawable.heart : R.drawable.empty_heart);
        heart2.setImageResource(hearts >= 2 ? R.drawable.heart : R.drawable.empty_heart);
        heart3.setImageResource(hearts >= 3 ? R.drawable.heart : R.drawable.empty_heart);
    }
}