package com.example.guessingnumber_fp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import com.example.guessingnumber_fp.R;

public class PlayActivity extends BaseActivity {
    int targetNumber, score = 0, hearts = 3, hints = 3, difficultyMax = 50;
    int gamesPlayed = 0, gamesWon = 0, gamesLost = 0, hintsUsed = 0;
    String difficulty = "easy";
    Random random = new Random();
    private boolean isNavigatingWithinApp = false;

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
    ImageButton btnTryAgain;
    Handler handler = new Handler();

    SharedPreferences prefs;

    private String originalRangeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            getWindow().setStatusBarColor(0xFF000000);
            setContentView(R.layout.activity_play);

            // Initialize background music for play activity
            prefs = getSharedPreferences("game_data", MODE_PRIVATE);
            boolean musicOn = prefs.getBoolean("music_on", true);
            MusicManager.setLooping(true);
            if (musicOn) {
                MusicManager.start(this, R.raw.bg_music_2);
            }

            // Initialize UI components
            initializeUIComponents();

            // Get difficulty and range from Intent
            difficulty = getIntent().getStringExtra("difficulty");
            if (difficulty == null) difficulty = "easy"; // Default to easy if not specified
            
            // Set difficulty max based on selected difficulty
            if ("easy".equals(difficulty)) {
                difficultyMax = 10;
            } else if ("medium".equals(difficulty)) {
                difficultyMax = 50;
            } else if ("hard".equals(difficulty)) {
                difficultyMax = 100;
            }
            
            String levelText = difficulty.toUpperCase() + " LEVEL";
            if (tvLevel != null) tvLevel.setText(levelText);
            originalRangeMessage = "We are thinking of a number between 1 and " + difficultyMax;
            if (tvRange != null) tvRange.setText(originalRangeMessage);

            startGameMusic();
            startNewGame();

            setupClickListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing game: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeUIComponents() {
        try {
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
            btnTryAgain = findViewById(R.id.btnTryAgain);

            // Essential components that must not be null
            if (etGuess == null || btnGuess == null) {
                throw new IllegalStateException("Essential game components are missing");
            }

            // Initialize default values for UI components
            if (tvHint != null) tvHint.setText("HINTS: " + hints);
            if (tvRange != null) tvRange.setText("We are thinking of a number between 1 and " + difficultyMax);
            if (btnTryAgain != null) btnTryAgain.setVisibility(View.GONE);
            
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
            throw e; // Re-throw to be caught by onCreate
        }
    }

    private void setupClickListeners() {
        btnGuess.setOnClickListener(v -> checkGuess());
        btnGiveUp.setOnClickListener(v -> giveUp());
        btnHint.setOnClickListener(v -> useHint());
        btnTryAgain.setOnClickListener(v -> {
            // Re-enable all game components
            if (btnGuess != null) {
                btnGuess.setEnabled(true);
                btnGuess.setClickable(true);
            }
            if (btnHint != null) {
                btnHint.setEnabled(true);
                btnHint.setClickable(true);
            }
            if (etGuess != null) {
                etGuess.setEnabled(true);
                etGuess.setText("");
            }
            if (btnGiveUp != null) {
                btnGiveUp.setText("Give Up");
            }
            
            // Start a new game
            startNewGame();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp) {
            MusicManager.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
        if (musicOn) {
            MusicManager.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isNavigatingWithinApp) {
            MusicManager.release();
        }
    }

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }

    private void startNewGame() {
        targetNumber = random.nextInt(difficultyMax) + 1;
        score = 0;
        hearts = 3;
        hasGuessedThisRound = false;
        
        // Set default hints based on difficulty
        if ("easy".equals(difficulty)) {
            hints = 3;
        } else if ("medium".equals(difficulty)) {
            hints = 5;
        } else if ("hard".equals(difficulty)) {
            hints = 10;
        }
        
        updateHearts();
        if (tvHint != null) tvHint.setText("HINTS: " + hints);
        if (etGuess != null) {
            etGuess.setText("");
            etGuess.setEnabled(true);
        }
        if (btnTryAgain != null) btnTryAgain.setVisibility(View.GONE);
        if (tvLevel != null) tvLevel.setText(difficulty.toUpperCase() + " LEVEL");
        
        // Enable all buttons
        if (btnGuess != null) {
            btnGuess.setEnabled(true);
            btnGuess.setClickable(true);
        }
        if (btnHint != null) {
            btnHint.setEnabled(true);
            btnHint.setClickable(true);
        }
        if (btnGiveUp != null) {
            btnGiveUp.setEnabled(true);
            btnGiveUp.setClickable(true);
            btnGiveUp.setText("Give Up");
        }
        
        if (tvRange != null) tvRange.setText(originalRangeMessage);
        
        // Log the target number for debugging
        Log.d("PlayActivity", "New game started with target number: " + targetNumber);
    }

    private void useHint() {
        if (hints > 0) {
            if (hasGuessedThisRound) {
                hints--;
                hintsUsed++;
                String currentUser = prefs.getString("current_user", "guest");
                prefs.edit().putInt("hints_used_" + difficulty + "_" + currentUser, prefs.getInt("hints_used_" + difficulty + "_" + currentUser, 0) + 1).apply();
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
                tvHintMessage.setText("Make a guess first!");
                tvHintMessage.setVisibility(View.VISIBLE);
                
                // Hide the message after 2 seconds
                handler.postDelayed(() -> {
                    tvHintMessage.setVisibility(View.GONE);
                }, 2000);
            }
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

    private boolean hasGuessedThisRound = false;
    
    private void checkGuess() {
        try {
            // Only check essential components
            if (etGuess == null || btnGuess == null) {
                Toast.makeText(this, "Game components not initialized properly", Toast.LENGTH_SHORT).show();
                return;
            }

            String guessStr = etGuess.getText().toString().trim();
            
            if (guessStr.isEmpty()) {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
                return;
            }

            int guess = Integer.parseInt(guessStr);
            
            if (guess < 1 || guess > difficultyMax) {
                Toast.makeText(this, "Please enter a number between 1 and " + difficultyMax, Toast.LENGTH_SHORT).show();
                return;
            }

            String currentUser = prefs.getString("current_user", "guest");

            if (!hasGuessedThisRound) {
                prefs.edit().putInt("games_played_" + difficulty + "_" + currentUser, 
                    prefs.getInt("games_played_" + difficulty + "_" + currentUser, 0) + 1).apply();
                hasGuessedThisRound = true;
            }

            if (guess == targetNumber) {
                // Show success message
                Toast.makeText(this, "Correct! Moving to next number!", Toast.LENGTH_SHORT).show();
                handleCorrectGuess(currentUser);
            } else {
                handleIncorrectGuess(guess, currentUser);
            }
            
            // Clear the input field after each guess
            etGuess.setText("");
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCorrectGuess(String currentUser) {
        score++;
        hearts = 3;
        updateHearts();
        
        if (score % 3 == 0) {
            hints++;
            prefs.edit().putInt("hints_" + difficulty + "_" + currentUser, hints).apply();
            if (isSoundOn()) {
                Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
            }
        }
        
        if (tvHint != null) tvHint.setText("HINTS: " + hints);
        
        // Update scores
        int userBest = prefs.getInt("highscore_" + difficulty + "_" + currentUser, 0);
        if (score > userBest) {
            prefs.edit().putInt("highscore_" + difficulty + "_" + currentUser, score).apply();
        }
        
        int globalHighscore = prefs.getInt("highscore_" + difficulty, 0);
        if (score > globalHighscore) {
            prefs.edit().putInt("highscore_" + difficulty, score).apply();
            prefs.edit().putString("highscore_" + difficulty + "_user", currentUser).apply();
        }
        
        prefs.edit().putInt("games_won_" + difficulty + "_" + currentUser, 
            prefs.getInt("games_won_" + difficulty + "_" + currentUser, 0) + 1).apply();
        
        targetNumber = random.nextInt(difficultyMax) + 1;
        if (etGuess != null) etGuess.setText("");
        if (tvRange != null) tvRange.setText(originalRangeMessage);
    }

    private void handleIncorrectGuess(int guess, String currentUser) {
        hearts--;
        updateHearts();
        
        if (hearts == 0) {
            int lost = prefs.getInt("games_lost_" + difficulty + "_" + currentUser, 0) + 1;
            prefs.edit().putInt("games_lost_" + difficulty + "_" + currentUser, lost).apply();

            if (tvLevel != null) tvLevel.setText("GAME OVER");
            if (btnGuess != null) {
                btnGuess.setEnabled(false);
                btnGuess.setClickable(false);
            }
            if (btnHint != null) {
                btnHint.setEnabled(false);
                btnHint.setClickable(false);
            }
            if (etGuess != null) {
                etGuess.setEnabled(false);
            }
            
            // Show both Try Again and Give Up buttons
            if (btnTryAgain != null) {
                btnTryAgain.setVisibility(View.VISIBLE);
            }
            if (btnGiveUp != null) {
                btnGiveUp.setText("Back to Menu");
            }
            
            // Show game over message with the target number
            Toast.makeText(this, "Game Over! The number was " + targetNumber, Toast.LENGTH_LONG).show();
        } else {
            if (tvRange != null) {
                String message = getMockingMessage(guess);
                tvRange.setText(message);
                // Also show whether the guess was too high or too low
                Toast.makeText(this, guess < targetNumber ? "Too low!" : "Too high!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void giveUp() {
        // If game is over (no hearts left), just return to main menu
        if (hearts <= 0) {
            isNavigatingWithinApp = true;
            finish();
            return;
        }

        // Otherwise show the confirmation dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Give Up?")
                .setMessage(hasGuessedThisRound ? 
                    "Are you sure you want to give up? This will count as a loss." :
                    "Are you sure you want to give up? This will not affect your stats.")
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    if (hasGuessedThisRound) {
                        String currentUser = prefs.getString("current_user", "guest");
                        int lost = prefs.getInt("games_lost_" + difficulty + "_" + currentUser, 0) + 1;
                        prefs.edit().putInt("games_lost_" + difficulty + "_" + currentUser, lost).apply();
                        AlertDialog resultDialog = new AlertDialog.Builder(this)
                            .setTitle("Gave Up")
                            .setMessage("The number was: " + targetNumber)
                            .setPositiveButton("OK", (d, w) -> {
                                isNavigatingWithinApp = true;
                                finish();
                            })
                            .create();
                        resultDialog.setOnShowListener(dialogInterface1 -> {
                            resultDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
                        });
                        resultDialog.show();
                    } else {
                        isNavigatingWithinApp = true;
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
        });
        
        dialog.show();
    }

    private void updateHearts() {
        heart1.setImageResource(hearts >= 1 ? R.drawable.heart : R.drawable.empty_heart);
        heart2.setImageResource(hearts >= 2 ? R.drawable.heart : R.drawable.empty_heart);
        heart3.setImageResource(hearts >= 3 ? R.drawable.heart : R.drawable.empty_heart);
    }
}