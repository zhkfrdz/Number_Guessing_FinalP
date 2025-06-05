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
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import com.example.guessingnumber_fp.R;
import android.os.Build;

public class PlayActivity extends BaseActivity {
    int targetNumber, score = 0, hearts = 3, maxHearts = 3, hints = 3, difficultyMax = 50;
    int correctGuessCounter = 0;
    int gamesPlayed = 0, gamesWon = 0, gamesLost = 0, hintsUsed = 0;
    String difficulty = "easy";
    Random random = new Random();
    private boolean isNavigatingWithinApp = false;
    private boolean isInGameFlow = false;
    private boolean isHintOnCooldown = false;
    private int hintCooldownSeconds = 5;
    private int previousGuess = 0; // Track the user's previous guess for comparison hints
    private String originalHintText = "HINT";
    private Runnable hintCooldownRunnable;

    // Mocking messages arrays
    private final String[] tooLowMessages = {
            "Wow, aiming that low? Are you even trying?",
            "That's cute. Add some digits, maybe you'll get close.",
            "This isn't basement, too low.",
            "Did you guess with your eyes closed?",
            "Colder than your last relationship. Try higher.",
            "This ain't your grade buddy.",
            "Did you guess in the coffin?"
    };

    private final String[] tooHighMessages = {
            "Relax, it's not your ego. Go lower.",
            "That number is as inflated as your confidence.",
            "Trying to touch the sun, are we? Bring it down.",
            "You overshot it like your life goals.",
            "You expect high huh? That's why you get hurt.",
            "Wow the numbers are flying, you ain't a bird.",
            "You ain't him buddy! Go lower."
    };

    TextView tvLevel, tvRange, tvHint, tvHintMessage;
    ImageButton btnHintInfo;
    EditText etGuess;
    ImageView heart1, heart2, heart3, heart4, heart5, heart6;
    Button btnGuess, btnGiveUp, btnHint, btnHelpPlay;
    ImageButton btnTryAgain;
    Handler handler = new Handler();

    SharedPreferences prefs;

    private String originalRangeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // Make the app full screen with immersive mode
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            
            super.onCreate(savedInstanceState);
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            
            // Set black status bar color in immersive mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(0xFF000000);
            }
            
            setContentView(R.layout.activity_play);

            // Initialize background music for play activity
            prefs = getSharedPreferences("game_data", MODE_PRIVATE);
            boolean musicOn = prefs.getBoolean("music_on", true);
            MusicManager.setLooping(true);

            // Initialize UI components
            initializeUIComponents();

            // Get difficulty and range from Intent
            difficulty = getIntent().getStringExtra("difficulty");
            if (difficulty == null) difficulty = "easy"; // Default to easy if not specified

            // Set difficulty max and hearts based on selected difficulty
            if ("easy".equals(difficulty)) {
                difficultyMax = 10;
                hints = 3;
                hearts = 3;
                maxHearts = 3;
            } else if ("medium".equals(difficulty)) {
                difficultyMax = 30;
                hints = 3;
                hearts = 4;
                maxHearts = 4;
            } else if ("hard".equals(difficulty)) {
                difficultyMax = 50;
                hints = 3;
                hearts = 5;
                maxHearts = 5;
            } else if ("impossible".equals(difficulty)) {
                difficultyMax = 100;
                hints = 3;
                hearts = 6;
                maxHearts = 6;
            }

            // Show the appropriate number of hearts based on difficulty
            if (heart4 != null) heart4.setVisibility(maxHearts >= 4 ? View.VISIBLE : View.GONE);
            if (heart5 != null) heart5.setVisibility(maxHearts >= 5 ? View.VISIBLE : View.GONE);
            if (heart6 != null) heart6.setVisibility(maxHearts >= 6 ? View.VISIBLE : View.GONE);

            String levelText = difficulty.toUpperCase() + " LEVEL";
            if (tvLevel != null) tvLevel.setText(levelText);
            originalRangeMessage = "We are thinking of a number between 1 and " + difficultyMax;
            if (tvRange != null) tvRange.setText(originalRangeMessage);

            // Set the appropriate difficulty image
            ImageView imgCats = findViewById(R.id.imgCats);
            if (imgCats != null) {
                switch (difficulty) {
                    case "easy":
                        imgCats.setImageResource(R.drawable.easy);
                        break;
                    case "medium":
                        imgCats.setImageResource(R.drawable.medium);
                        break;
                    case "hard":
                        imgCats.setImageResource(R.drawable.hard);
                        break;
                    case "impossible":
                        imgCats.setImageResource(R.drawable.impossible);
                        break;
                    default:
                        imgCats.setImageResource(R.drawable.play);
                        break;
                }
            }

            startLevelMusic();
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
            heart4 = findViewById(R.id.heart4);
            heart5 = findViewById(R.id.heart5);
            heart6 = findViewById(R.id.heart6);
            btnGuess = findViewById(R.id.btnGuess);
            btnGiveUp = findViewById(R.id.btnGiveUp);
            btnHint = findViewById(R.id.btnHintButton);
            btnTryAgain = findViewById(R.id.btnTryAgain);
            btnHintInfo = findViewById(R.id.btnHintInfo);

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

    @Override
    public void onBackPressed() {
        // Play back button sound directly
        MediaPlayer backBtnPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
        if (backBtnPlayer != null) {
            backBtnPlayer.setLooping(false);
            backBtnPlayer.setOnCompletionListener(mp -> mp.release());
            backBtnPlayer.start();
        }
        
        // Go back to SelectDifficultyFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("show_select_difficulty", true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        startLevelMusic();
        
        // Re-enable immersive mode on resume
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setupClickListeners() {
        btnGuess.setOnClickListener(v -> {
            if (isSoundOn()) {
                MediaPlayer guessPlayer = MediaPlayer.create(this, R.raw.guess_st);
                if (guessPlayer != null) {
                    guessPlayer.setLooping(false);
                    guessPlayer.setOnCompletionListener(mp -> mp.release());
                    guessPlayer.start();
                }
            }
            checkGuess();
        });

        btnGiveUp.setOnClickListener(v -> {
            if ("Back to Menu".equals(btnGiveUp.getText().toString())) {
                // Play back button sound
                if (isSoundOn()) {
                    MediaPlayer backBtnPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
                    if (backBtnPlayer != null) {
                        backBtnPlayer.setLooping(false);
                        backBtnPlayer.setOnCompletionListener(mp -> mp.release());
                        backBtnPlayer.start();
                    }
                }
                // If button says "Back to Menu", just go back to the difficulty selection screen
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                finish();
            } else {
                // Play give up button sound
                if (isSoundOn()) {
                    MediaPlayer giveUpPlayer = MediaPlayer.create(this, R.raw.quit_st);
                    if (giveUpPlayer != null) {
                        giveUpPlayer.setLooping(false);
                        giveUpPlayer.setOnCompletionListener(mp -> mp.release());
                        giveUpPlayer.start();
                    }
                }
                // If button says "Give Up", show the give up confirmation dialog
                giveUp();
            }
        });

        btnHint.setOnClickListener(v -> {
            // Check if hint button is on cooldown
            if (!isHintOnCooldown) {
                // Save original hint text if not saved yet
                if (originalHintText.equals("HINT") && btnHint.getText() != null) {
                    originalHintText = btnHint.getText().toString();
                }

                // Play hint_st sound once
                if (isSoundOn()) {
                    MediaPlayer hintPlayer = MediaPlayer.create(this, R.raw.hint_st);
                    if (hintPlayer != null) {
                        hintPlayer.setLooping(false);
                        hintPlayer.setOnCompletionListener(mp -> {
                            mp.release();
                        });
                        hintPlayer.start();
                    }
                }
                useHint();

                // Set cooldown
                isHintOnCooldown = true;
                btnHint.setAlpha(0.5f); // Visual feedback that button is disabled

                // Start countdown display
                startHintCooldownTimer();
            } else {
                // Optional: Show a message that hint is on cooldown
                Toast.makeText(PlayActivity.this, "Hint on cooldown!", Toast.LENGTH_SHORT).show();
            }
        });

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
                btnGiveUp.setText("Back to Menu"); // Reset to Back to Menu for new game
            }
            // Start a new game
            startNewGame();
        });

        btnHintInfo.setOnClickListener(v -> {
            showHintInfoDialog();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Do NOT stop or release music here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do NOT stop or release music here
    }

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }

    private void startNewGame() {
        // Generate a new target number
        targetNumber = random.nextInt(difficultyMax) + 1;

        // Reset game state
        // Set hearts based on difficulty
        if ("easy".equals(difficulty)) {
            hearts = 3;
        } else if ("medium".equals(difficulty)) {
            hearts = 4;
        } else if ("hard".equals(difficulty)) {
            hearts = 5;
        } else if ("impossible".equals(difficulty)) {
            hearts = 6;
        } else {
            hearts = 3; // Default
        }

        correctGuessCounter = 0;
        hasGuessedThisRound = false;
        previousGuess = 0; // Reset previous guess for new game
        updateHearts();
        // Set default hints based on difficulty
        if ("easy".equals(difficulty)) {
            hints = 3;
        } else if ("medium".equals(difficulty)) {
            hints = 3;
        } else if ("hard".equals(difficulty)) {
            hints = 3;
        } else if ("impossible".equals(difficulty)) {
            hints = 3;
        }

        if (tvHint != null) tvHint.setText("HINTS: " + hints);

        // Update scores
        int userBest = prefs.getInt("highscore_" + difficulty, 0);
        if (score > userBest) {
            prefs.edit().putInt("highscore_" + difficulty, score).apply();
        }

        int globalHighscore = prefs.getInt("highscore_" + difficulty, 0);
        if (score > globalHighscore) {
            prefs.edit().putInt("highscore_" + difficulty, score).apply();
            prefs.edit().putString("highscore_" + difficulty + "_user", "guest").apply();
        }

        prefs.edit().putInt("games_played_" + difficulty, prefs.getInt("games_played_" + difficulty, 0) + 1).apply();

        // Reset UI elements
        if (etGuess != null) {
            etGuess.setText("");
            etGuess.setEnabled(true);
            etGuess.setAlpha(1f);
        }
        if (tvRange != null) {
            tvRange.setText(originalRangeMessage);
            tvRange.setAlpha(1f);
        }

        // Reset buttons
        if (btnGuess != null) {
            btnGuess.setEnabled(true);
            btnGuess.setClickable(true);
        }
        if (btnHint != null) {
            btnHint.setEnabled(true);
            btnHint.setClickable(true);
        }
        if (btnGiveUp != null) {
            btnGiveUp.setText("Back to Menu");
            btnGiveUp.setEnabled(true);
            btnGiveUp.setClickable(true);
        }
        if (btnTryAgain != null) {
            btnTryAgain.setVisibility(View.GONE);
        }

        // Log the target number for debugging
        Log.d("PlayActivity", "New target number: " + targetNumber);
    }

    /**
     * Returns a random roast message when the player uses too many hints
     */
    private String getRoastMessage() {
        String[] roasts = {
                "Try counting on your fingers!",
                "Numbers isn't your thing, huh?",
                "Guessing with eyes closed?",
                "Need more help?",
                "Try easier mode next time!",
                "Hint Expert!",
                "Guessing champion...",
                "Interesting strategy...",
                "Random number generator!",
                "My grandma guesses better!",
                "Ground level IQ!",
                "Do you hear that? *dumbmeow....",
                "You're cooked.",
                "Should I make a tutorial just for you?"
        };
        return roasts[random.nextInt(roasts.length)];
    }

    private void useHint() {
        if (hints > 0) {
            if (hasGuessedThisRound) {
                hints--;
                hintsUsed++;
                String currentUser = prefs.getString("current_user", "guest");
                prefs.edit().putInt("hints_used_" + difficulty + "_" + currentUser, prefs.getInt("hints_used_" + difficulty + "_" + currentUser, 0) + 1).apply();
                tvHint.setText("HINTS: " + hints);

                // Determine when to show the roast based on difficulty
                int roastThreshold = ("hard".equals(difficulty) || "impossible".equals(difficulty)) ? 5 : 3;

                // Check if we've reached the threshold for roasting
                if (hintsUsed == roastThreshold) {
                    // Play sound effect for the roast - using hint sound since it's a hint-related feature
                    MediaPlayer roastSound = MediaPlayer.create(this, R.raw.hint_st);
                    roastSound.start();

                    // Roast the player and reveal the answer
                    String roastMessage = getRoastMessage() + "\nThe answer is " + targetNumber + "!";
                    tvHintMessage.setText(roastMessage);
                    tvHintMessage.setVisibility(View.VISIBLE);

                    // Hide the message after 5 seconds to be consistent with other hints
                    handler.postDelayed(() -> {
                        tvHintMessage.setVisibility(View.GONE);
                        // Optionally start a new round after revealing the answer
                        // startNewRound();
                    }, 5000);

                    return; // Skip normal hint processing
                }

                // Normal hint processing (before reaching the roast threshold)
                // Use the HintManager to generate an appropriate hint based on difficulty
                String hintMessage = HintManagerNew.generateHint(targetNumber, difficultyMax, difficulty);

                // Show hint message in custom TextView
                tvHintMessage.setText(hintMessage);
                tvHintMessage.setVisibility(View.VISIBLE);

                // Hide the message after 5 seconds to give more time to read
                handler.postDelayed(() -> {
                    tvHintMessage.setVisibility(View.GONE);
                }, 5000);
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

            // Store the current guess as previousGuess for future hint comparisons
            previousGuess = guess;
            // Update the last user guess for mathematical hints
            HintManagerNew.setLastUserGuess(guess);

            String currentUser = prefs.getString("current_user", "guest");

            if (!hasGuessedThisRound) {
                prefs.edit().putInt("games_played_" + difficulty + "_" + currentUser,
                        prefs.getInt("games_played_" + difficulty + "_" + currentUser, 0) + 1).apply();
                hasGuessedThisRound = true;

                // Change the button text from "Back to Menu" to "Give Up" after first guess
                if (btnGiveUp != null && "Back to Menu".equals(btnGiveUp.getText().toString())) {
                    btnGiveUp.setText("Give Up");
                }
            }

            if (guess == targetNumber) {
                // Play correct answer sound
                if (isSoundOn()) {
                    MediaPlayer correctPlayer = MediaPlayer.create(this, R.raw.correct);
                    if (correctPlayer != null) {
                        correctPlayer.setLooping(false);
                        correctPlayer.setOnCompletionListener(mp -> mp.release());
                        correctPlayer.start();
                    }
                }

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
        hearts = maxHearts;
        updateHearts();
        correctGuessCounter++;

        // Add hints based on difficulty and correct guess counter
        if ("easy".equals(difficulty) && correctGuessCounter >= 3) {
            hints++;
            correctGuessCounter = 0;
            if (isSoundOn()) {
                Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
            }
        } else if ("medium".equals(difficulty) && correctGuessCounter >= 2) {
            hints++;
            correctGuessCounter = 0;
            if (isSoundOn()) {
                Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
            }
        } else if ("hard".equals(difficulty) && correctGuessCounter >= 2) {
            hints++;
            correctGuessCounter = 0;
            if (isSoundOn()) {
                Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
            }
        } else if ("impossible".equals(difficulty) && correctGuessCounter >= 1) {
            // 25% chance to get a hint in impossible mode
            if (random.nextInt(4) == 0) { // 1 in 4 chance (25%)
                hints++;
                if (isSoundOn()) {
                    Toast.makeText(this, "You earned a hint!", Toast.LENGTH_SHORT).show();
                }
            }
            correctGuessCounter = 0;
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
            if (etGuess != null) etGuess.setAlpha(0.5f);
            if (tvRange != null) tvRange.setAlpha(0.5f);

            if (btnHint != null) {
                btnHint.setEnabled(false);
                btnHint.setClickable(false);
            }
            if (etGuess != null) {
                etGuess.setEnabled(false);
            }
            // Hide the retry button in the main layout
            if (btnTryAgain != null) {
                btnTryAgain.setVisibility(View.GONE);
            }
            if (btnGiveUp != null) {
                btnGiveUp.setText("Back to Menu");
            }

            // Show no lives left dialog with image and sound
            final MediaPlayer[] noLivesPlayer = new MediaPlayer[1];
            if (isSoundOn()) {
                noLivesPlayer[0] = MediaPlayer.create(this, R.raw.no_lives_st);
                if (noLivesPlayer[0] != null) {
                    noLivesPlayer[0].setLooping(false);
                    noLivesPlayer[0].setOnCompletionListener(mp -> mp.release());
                    noLivesPlayer[0].start();
                }
            }
            // Create a custom dialog with polished UI
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create a custom view for the dialog
            LinearLayout dialogLayout = new LinearLayout(this);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(48, 35, 48, 48);
            dialogLayout.setGravity(Gravity.CENTER);
            dialogLayout.setBackgroundResource(R.drawable.dialog_gradient_bg);

            // Game over image
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.gameo);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
            imgParams.gravity = Gravity.CENTER;
            imgParams.setMargins(0, 0, 0, 24);
            image.setLayoutParams(imgParams);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dialogLayout.addView(image);

            // Title with styling
            TextView title = new TextView(this);
            title.setText("NO LIVES LEFT");
            title.setTextSize(24);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.audiowide));
            title.setTextColor(0xFFFF6B4A);
            title.setPadding(0, 0, 0, 16);
            dialogLayout.addView(title);

            // Number reveal
            TextView numberReveal = new TextView(this);
            numberReveal.setText("The number was: " + targetNumber);
            numberReveal.setTextSize(18);
            numberReveal.setGravity(Gravity.CENTER);
            numberReveal.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
            numberReveal.setTextColor(0xFFFFFFFF);
            numberReveal.setPadding(0, 0, 0, 32);
            dialogLayout.addView(numberReveal);

            // Button container
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(Gravity.CENTER);

            // Retry button
            Button retryBtn = new Button(this);
            retryBtn.setText("RETRY");
            retryBtn.setTextColor(0xFFFFFFFF);
            retryBtn.setBackgroundResource(R.drawable.rounded_red_button);
            LinearLayout.LayoutParams retryParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            retryParams.setMargins(8, 0, 8, 0);
            retryBtn.setLayoutParams(retryParams);
            buttonLayout.addView(retryBtn);

            // Back to Menu button
            Button backBtn = new Button(this);
            backBtn.setText("BACK TO MENU");
            backBtn.setTextColor(0xFFFFFFFF);
            backBtn.setBackgroundResource(R.drawable.rounded_red_button);
            LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            backParams.setMargins(8, 0, 8, 0);
            backBtn.setLayoutParams(backParams);
            buttonLayout.addView(backBtn);

            dialogLayout.addView(buttonLayout);

            builder.setView(dialogLayout);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();

            // Set transparent background for rounded corners
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            retryBtn.setOnClickListener(v -> {
                // Stop no lives sound if it's playing
                if (noLivesPlayer[0] != null && noLivesPlayer[0].isPlaying()) {
                    noLivesPlayer[0].stop();
                    noLivesPlayer[0].release();
                    noLivesPlayer[0] = null;
                }

                // Play button sound for Retry
                if (isSoundOn()) {
                    MediaPlayer buttonPlayer = MediaPlayer.create(this, R.raw.cat_buttons);
                    if (buttonPlayer != null) {
                        buttonPlayer.setLooping(false);
                        buttonPlayer.setOnCompletionListener(mp -> mp.release());
                        buttonPlayer.start();
                    }
                }

                dialog.dismiss();
                // Re-enable all game components and start a new game
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
                    etGuess.setAlpha(1f);
                }
                if (tvRange != null) tvRange.setAlpha(1f);
                if (btnGiveUp != null) {
                    btnGiveUp.setText("Back to Menu");
                }
                startNewGame();
            });
            backBtn.setOnClickListener(v -> {
                // Stop no lives sound if it's playing
                if (noLivesPlayer[0] != null && noLivesPlayer[0].isPlaying()) {
                    noLivesPlayer[0].stop();
                    noLivesPlayer[0].release();
                    noLivesPlayer[0] = null;
                }

                // Play back button sound
                if (isSoundOn()) {
                    MediaPlayer backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
                    if (backButtonPlayer != null) {
                        backButtonPlayer.setLooping(false);
                        backButtonPlayer.setOnCompletionListener(mp -> mp.release());
                        backButtonPlayer.start();
                    }
                }

                dialog.dismiss();
                finish();
            });
            dialog.show();
            // Ensure rounded corners are visible
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            // Show game over message with the target number
            // Toast.makeText(this, "Game Over! The number was " + targetNumber, Toast.LENGTH_LONG).show();
        } else {
            if (tvRange != null) {
                String message = getMockingMessage(guess);
                tvRange.setText(message);
                // Also show whether the guess was too high or too low
                // Toast.makeText(this, guess < targetNumber ? "Too low!" : "Too high!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void giveUp() {
        // If game is over (no hearts left), just return to select difficulty
        if (hearts <= 0) {
            isNavigatingWithinApp = true;
            isInGameFlow = false;
            finish();
        } else {
            // Create a custom confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create a custom view for the confirmation dialog
            LinearLayout confirmLayout = new LinearLayout(this);
            confirmLayout.setOrientation(LinearLayout.VERTICAL);
            confirmLayout.setPadding(48, 35, 48, 48);
            confirmLayout.setGravity(Gravity.CENTER);
            confirmLayout.setBackgroundResource(R.drawable.dialog_gradient_bg);

            // Title with styling
            TextView confirmTitle = new TextView(this);
            confirmTitle.setText("GIVE UP?");
            confirmTitle.setTextSize(24);
            confirmTitle.setGravity(Gravity.CENTER);
            confirmTitle.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.audiowide));
            confirmTitle.setTextColor(0xFFFF6B4A);
            confirmTitle.setPadding(0, 0, 0, 24);
            confirmLayout.addView(confirmTitle);

            // Message
            TextView confirmMessage = new TextView(this);
            confirmMessage.setText(hasGuessedThisRound ?
                    "Are you sure you want to give up?\nThis will count as a loss." :
                    "Are you sure you want to give up?\nThis will not affect your stats.");
            confirmMessage.setTextSize(16);
            confirmMessage.setGravity(Gravity.CENTER);
            confirmMessage.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
            confirmMessage.setTextColor(0xFFFFFFFF);
            confirmMessage.setLineSpacing(8, 1);
            confirmMessage.setPadding(0, 0, 0, 32);
            confirmLayout.addView(confirmMessage);

            // Button container
            LinearLayout buttonContainer = new LinearLayout(this);
            buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
            buttonContainer.setGravity(Gravity.CENTER);

            // Yes button
            Button yesButton = new Button(this);
            yesButton.setText("YES");
            yesButton.setTextColor(0xFFFFFFFF);
            yesButton.setBackgroundResource(R.drawable.rounded_red_button);
            LinearLayout.LayoutParams yesParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            yesParams.setMargins(8, 0, 8, 0);
            yesButton.setLayoutParams(yesParams);

            // No button
            Button noButton = new Button(this);
            noButton.setText("NO");
            noButton.setTextColor(0xFFFFFFFF);
            noButton.setBackgroundResource(R.drawable.rounded_red_button);
            LinearLayout.LayoutParams noParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            noParams.setMargins(8, 0, 8, 0);
            noButton.setLayoutParams(noParams);

            buttonContainer.addView(yesButton);
            buttonContainer.addView(noButton);
            confirmLayout.addView(buttonContainer);

            builder.setView(confirmLayout);
            AlertDialog confirmDialog = builder.create();

            // Set transparent background for rounded corners
            if (confirmDialog.getWindow() != null) {
                confirmDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            // Button click handlers
            yesButton.setOnClickListener(v -> {
                // Play give up sound for Yes
                if (isSoundOn()) {
                    MediaPlayer giveUpPlayer = MediaPlayer.create(this, R.raw.giveup);
                    if (giveUpPlayer != null) {
                        giveUpPlayer.setLooping(false);
                        giveUpPlayer.setOnCompletionListener(mp -> mp.release());
                        giveUpPlayer.start();
                    }
                }

                confirmDialog.dismiss();

                if (hasGuessedThisRound) {
                    String currentUser = prefs.getString("current_user", "guest");
                    int lost = prefs.getInt("games_lost_" + difficulty + "_" + currentUser, 0) + 1;
                    prefs.edit().putInt("games_lost_" + difficulty + "_" + currentUser, lost).apply();

                    // Show game over dialog with the gameo.png image
                    AlertDialog.Builder resultBuilder = new AlertDialog.Builder(this);
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(48, 35, 48, 48);
                    layout.setGravity(Gravity.CENTER);
                    layout.setBackgroundResource(R.drawable.dialog_gradient_bg);

                    // Game over image
                    ImageView gameOverImage = new ImageView(this);
                    gameOverImage.setImageResource(R.drawable.gameover);
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
                    imgParams.gravity = Gravity.CENTER;
                    imgParams.setMargins(0, 0, 0, 24);
                    gameOverImage.setLayoutParams(imgParams);
                    gameOverImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    layout.addView(gameOverImage);

                    // Title
                    TextView title = new TextView(this);
                    title.setText("FORFEITED");
                    title.setTextSize(24);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.audiowide));
                    title.setTextColor(0xFFFF6B4A);
                    title.setPadding(0, 0, 0, 16);
                    layout.addView(title);

                    // Number reveal
                    TextView numberReveal = new TextView(this);
                    numberReveal.setText("The number was: " + targetNumber);
                    numberReveal.setTextSize(18);
                    numberReveal.setGravity(Gravity.CENTER);
                    numberReveal.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
                    numberReveal.setTextColor(0xFFFFFFFF);
                    numberReveal.setPadding(0, 0, 0, 32);
                    layout.addView(numberReveal);

                    // Back to menu button
                    Button backButton = new Button(this);
                    backButton.setText("BACK TO MENU");
                    backButton.setTextColor(0xFFFFFFFF);
                    backButton.setBackgroundResource(R.drawable.rounded_red_button);
                    LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    backButton.setLayoutParams(backParams);
                    layout.addView(backButton);

                    resultBuilder.setView(layout);
                    AlertDialog resultDialog = resultBuilder.create();

                    // Set transparent background for rounded corners
                    if (resultDialog.getWindow() != null) {
                        resultDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }

                    backButton.setOnClickListener(btn -> {
                        // Play back button sound
                        if (isSoundOn()) {
                            MediaPlayer backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
                            if (backButtonPlayer != null) {
                                backButtonPlayer.setLooping(false);
                                backButtonPlayer.setOnCompletionListener(mp -> mp.release());
                                backButtonPlayer.start();
                            }
                        }

                        resultDialog.dismiss();
                        finish();
                    });

                    resultDialog.show();
                } else {
                    finish();
                }
            });

            noButton.setOnClickListener(v -> {
                // Play back button sound for No
                if (isSoundOn()) {
                    MediaPlayer backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
                    if (backButtonPlayer != null) {
                        backButtonPlayer.setLooping(false);
                        backButtonPlayer.setOnCompletionListener(mp -> mp.release());
                        backButtonPlayer.start();
                    }
                }

                confirmDialog.dismiss();
            });

            confirmDialog.show();
        }
    }

    private void updateHearts() {
        heart1.setImageResource(hearts >= 1 ? R.drawable.heart : R.drawable.empty_heart);
        heart2.setImageResource(hearts >= 2 ? R.drawable.heart : R.drawable.empty_heart);
        heart3.setImageResource(hearts >= 3 ? R.drawable.heart : R.drawable.empty_heart);

        // Only update additional hearts if they're visible for this difficulty
        if (heart4 != null && heart4.getVisibility() == View.VISIBLE) {
            heart4.setImageResource(hearts >= 4 ? R.drawable.heart : R.drawable.empty_heart);
        }
        if (heart5 != null && heart5.getVisibility() == View.VISIBLE) {
            heart5.setImageResource(hearts >= 5 ? R.drawable.heart : R.drawable.empty_heart);
        }
        if (heart6 != null && heart6.getVisibility() == View.VISIBLE) {
            heart6.setImageResource(hearts >= 6 ? R.drawable.heart : R.drawable.empty_heart);
        }
    }

    @Override
    public void finish() {
        isNavigatingWithinApp = true;
        isInGameFlow = true;
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("targetNumber", targetNumber);
        outState.putInt("score", score);
        outState.putInt("hearts", hearts);
        outState.putInt("hints", hints);
        outState.putInt("correctGuessCounter", correctGuessCounter);
        outState.putBoolean("hasGuessedThisRound", hasGuessedThisRound);
    }

    private int getMusicResForDifficulty() {
        switch (difficulty) {
            case "easy":
                return R.raw.ez_bg_music;
            case "medium":
                return R.raw.medium_bg_music;
            case "hard":
                return R.raw.hardy; // Updated to use hardy.mpeg for hard difficulty
            case "impossible":
                return R.raw.hard_bg_music; // Using hard_bg_music.mp3 for impossible difficulty
            default:
                return R.raw.ez_bg_music;
        } // Added the missing closing brace here
    }

    private void startLevelMusic() {
        // Only start music if it's enabled in settings
        if (!dataManager.isMusicEnabled()) {
            return;
        }

        // Set game flow flag to true
        isInGameFlow = true;

        // Get music resource based on difficulty
        int musicRes = getMusicResForDifficulty();

        // Set looping and start music with a small delay to ensure it starts properly
        MusicManager.setLooping(true);

        // Use a handler to add a small delay before starting music
        // This helps when navigating quickly between difficulty levels
        new Handler().postDelayed(() -> {
            if (isInGameFlow) { // Only start if we're still in the game flow
                MusicManager.start(this, musicRes);
            }
        }, 100); // 100ms delay is enough to ensure proper initialization
    }


    /**
     * Starts a countdown timer for the hint button cooldown
     */
    private void startHintCooldownTimer() {
        // Cancel any existing cooldown runnable
        if (hintCooldownRunnable != null) {
            handler.removeCallbacks(hintCooldownRunnable);
        }

        // Set initial countdown value
        final int[] secondsLeft = {hintCooldownSeconds};

        // Update button text to show countdown
        btnHint.setText(secondsLeft[0] + "");

        // Create a new runnable for the countdown
        hintCooldownRunnable = new Runnable() {
            @Override
            public void run() {
                secondsLeft[0]--;

                if (secondsLeft[0] > 0) {
                    // Update button text with remaining seconds
                    btnHint.setText(secondsLeft[0] + "");
                    // Schedule next update in 1 second
                    handler.postDelayed(this, 1000);
                } else {
                    // Cooldown complete, restore button
                    isHintOnCooldown = false;
                    btnHint.setAlpha(1.0f);
                    btnHint.setText(originalHintText);
                }
            }
        };

        // Start the countdown
        handler.postDelayed(hintCooldownRunnable, 1000);
    }

    private void showHintInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom view for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 35, 48, 48);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF000000);

        // Title with styling
        TextView title = new TextView(this);
        title.setText("HINT SYSTEM");
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.audiowide));
        title.setTextColor(0xFFFF6B4A);
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        // Content based on difficulty
        String messageTitle = "";
        String messageContent = "";
        int iconResource = 0;

        if ("easy".equals(difficulty)) {
            messageTitle = "EASY MODE";
            messageContent = "• Start with 3 hints\n• Earn +1 hint for every 3 correct guesses";
            iconResource = R.drawable.easy;
        } else if ("medium".equals(difficulty)) {
            messageTitle = "MEDIUM MODE";
            messageContent = "• Start with 3 hints\n• Earn +1 hint for every 2 correct guesses";
            iconResource = R.drawable.medium;
        } else if ("hard".equals(difficulty)) {
            messageTitle = "HARD MODE";
            messageContent = "• Start with 3 hints\n• Earn +1 hint for every 2 correct guesses";
            iconResource = R.drawable.hard;
        } else if ("impossible".equals(difficulty)) {
            messageTitle = "IMPOSSIBLE MODE";
            messageContent = "• Start with 3 hints\n• Earn +1 hint for every correct guess (25% chance)";
            iconResource = R.drawable.impossible;
        }
        // Add small difficulty icon
        if (iconResource != 0) {
            ImageView icon = new ImageView(this);
            icon.setImageResource(iconResource);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(200, 200);
            imgParams.gravity = Gravity.CENTER;
            imgParams.setMargins(0, 0, 0, 16);
            icon.setLayoutParams(imgParams);
            layout.addView(icon);
        }

        // Add difficulty title
        TextView difficultyTitle = new TextView(this);
        difficultyTitle.setText(messageTitle);
        difficultyTitle.setTextSize(18);
        difficultyTitle.setGravity(Gravity.CENTER);
        difficultyTitle.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
        difficultyTitle.setTextColor(0xFFFFFFFF);
        difficultyTitle.setPadding(0, 8, 0, 16);
        layout.addView(difficultyTitle);

        // Add content
        TextView content = new TextView(this);
        content.setText(messageContent);
        content.setTextSize(16);
        content.setGravity(Gravity.LEFT);
        content.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
        content.setTextColor(0xFFFF6B4A);
        content.setLineSpacing(8, 1);
        content.setPadding(16, 0, 0, 24);
        layout.addView(content);

        // Set the custom view to the builder
        builder.setView(layout);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }
}
