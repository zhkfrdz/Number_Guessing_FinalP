package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.graphics.Color;
import android.widget.TextView;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;

import java.util.Map;

public class StatsActivity extends BaseActivity {
    private String currentDifficulty = "easy";
    private GameDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);
        String currentUser = dataManager.getCurrentUser();

        startMenuMusic();

        // Setup difficulty spinner
        Spinner spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.difficulties_array)) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] difficulties = {"easy", "medium", "hard", "impossible"};
                currentDifficulty = difficulties[position];
                
                // Use SQLite if available, otherwise fall back to SharedPreferences
                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    displayStatsFromSQLite(currentDifficulty);
                } else {
                    displayStats(prefs, dataManager.getCurrentUser(), currentDifficulty);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial stats display
        if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
            displayStatsFromSQLite(currentDifficulty);
        } else {
            displayStats(prefs, currentUser, currentDifficulty);
        }

        ((ImageButton)findViewById(R.id.btnBackStats)).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            finish();
        });
    }

    /**
     * Display statistics from SharedPreferences (legacy method)
     */
    private void displayStats(SharedPreferences prefs, String user, String difficulty) {
        int gamesPlayed = prefs.getInt("games_played_" + difficulty + "_" + user, 0);
        int gamesWon = prefs.getInt("games_won_" + difficulty + "_" + user, 0);
        int gamesLost = prefs.getInt("games_lost_" + difficulty + "_" + user, 0);
        int hintsUsed = prefs.getInt("hints_used_" + difficulty + "_" + user, 0);
        int bestScore = prefs.getInt("highscore_" + difficulty + "_" + user, 0);

        updateStatsDisplay(gamesPlayed, gamesWon, gamesLost, hintsUsed, bestScore);
    }
    
    /**
     * Display statistics from SQLite database
     */
    private void displayStatsFromSQLite(String difficulty) {
        String username = dataManager.getCurrentUser();
        
        try {
            // Get all stats for the user and difficulty from the database
            Map<String, Integer> stats = dataManager.getStatsForUser(username, difficulty);
            
            // Extract the values we need
            int gamesPlayed = stats.getOrDefault("games_played", 0);
            int gamesWon = stats.getOrDefault("games_won", 0);
            int gamesLost = stats.getOrDefault("games_lost", 0);
            int hintsUsed = stats.getOrDefault("hints_used", 0);
            int bestScore = stats.getOrDefault("best_score", 0);
            
            // Update the UI with the stats
            updateStatsDisplay(gamesPlayed, gamesWon, gamesLost, hintsUsed, bestScore);
            
        } catch (Exception e) {
            // If there's an error, fall back to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
            displayStats(prefs, username, difficulty);
        }
    }
    
    /**
     * Update the UI with the given statistics
     */
    private void updateStatsDisplay(int gamesPlayed, int gamesWon, int gamesLost, int hintsUsed, int bestScore) {
        ((TextView)findViewById(R.id.tvGamesPlayed)).setText(String.valueOf(gamesPlayed));
        ((TextView)findViewById(R.id.tvGamesWon)).setText(String.valueOf(gamesWon));
        ((TextView)findViewById(R.id.tvGamesLost)).setText(String.valueOf(gamesLost));
        ((TextView)findViewById(R.id.tvHintsUsed)).setText(String.valueOf(hintsUsed));
        
        // Calculate and display win rate
        float winRate = gamesPlayed > 0 ? (float)gamesWon / gamesPlayed * 100 : 0;
        ((TextView)findViewById(R.id.tvWinRate)).setText(String.format("%.1f%%", winRate));
        
        // Display best score
        ((TextView)findViewById(R.id.tvBestScore)).setText(String.valueOf(bestScore));
    }
    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }

}