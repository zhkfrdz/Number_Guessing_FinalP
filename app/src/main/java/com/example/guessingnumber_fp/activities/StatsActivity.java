package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class StatsActivity extends AppCompatActivity {
    String[] levels = {"Easy", "Medium", "Hard"};
    String difficulty = "easy";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
        setContentView(R.layout.activity_stats);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        Spinner spinner = findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                difficulty = levels[position].toLowerCase();
                updateStats();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ((ImageButton)findViewById(R.id.btnBackStats)).setOnClickListener(v -> finish());
        updateStats();
    }

    /**
     * Update all stats fields for the selected difficulty.
     * Handles empty/zero stats gracefully and updates all UI fields.
     */
    private void updateStats() {
        String currentUser = prefs.getString("current_user", "guest");
        // Per-user stats
        int gamesPlayed = prefs.getInt("games_played_" + difficulty + "_" + currentUser, 0);
        int correctGuesses = prefs.getInt("games_won_" + difficulty + "_" + currentUser, 0);
        int wrongGuesses = prefs.getInt("games_lost_" + difficulty + "_" + currentUser, 0);
        int hintsUsed = prefs.getInt("hints_used_" + difficulty + "_" + currentUser, 0);
        // Best score for user (optional, can show global if desired)
        int bestScore = prefs.getInt("highscore_" + difficulty + "_" + currentUser, 0);
        // Global highscore (for display, if you want)
        // int globalHighscore = prefs.getInt("highscore_" + difficulty, 0);

        TextView tvGamesPlayed = findViewById(R.id.tvGamesPlayed);
        TextView tvCorrectGuesses = findViewById(R.id.tvGamesWon);
        TextView tvWrongGuesses = findViewById(R.id.tvGamesLost);
        TextView tvHintsUsed = findViewById(R.id.tvHintsUsed);
        TextView tvWinRate = findViewById(R.id.tvWinRate);
        TextView tvBestScore = findViewById(R.id.tvBestScore);

        tvGamesPlayed.setText(String.valueOf(gamesPlayed));
        tvCorrectGuesses.setText(String.valueOf(correctGuesses));
        tvWrongGuesses.setText(String.valueOf(wrongGuesses));
        tvHintsUsed.setText(String.valueOf(hintsUsed));
        // Calculate win rate
        String winRateStr = "0%";
        if (gamesPlayed > 0) {
            double winRate = ((double) correctGuesses / (double) gamesPlayed) * 100.0;
            winRateStr = String.format("%.1f%%", winRate);
        }
        tvWinRate.setText(winRateStr);
        tvBestScore.setText(String.valueOf(bestScore));
    }
}