package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighscoresActivity extends BaseActivity {

    private MediaPlayer backButtonPlayer;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    private String currentDifficulty = "easy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);
        startMenuMusic();

        backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
        
        // Setup difficulty spinner
        Spinner spinnerDifficulty = findViewById(R.id.spinnerHighscoreDifficulty);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Easy", "Medium", "Hard", "Impossible"}) {
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
                updateDifficultyTitle();
                
                // Use SQLite if available, otherwise fall back to SharedPreferences
                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    displayLeaderboardFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                } else {
                    displayLeaderboard(prefs, currentDifficulty, R.id.tvHighscoreContent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Initial display
        updateDifficultyTitle();
        
        // Use SQLite if available, otherwise fall back to SharedPreferences
        if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
            displayLeaderboardFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
        } else {
            displayLeaderboard(prefs, currentDifficulty, R.id.tvHighscoreContent);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            playBackButtonSound();
            isNavigatingWithinApp = true;
            finish();
        });
    }

    private void updateDifficultyTitle() {
        TextView tvDifficultyTitle = findViewById(R.id.tvDifficultyTitle);
        if (tvDifficultyTitle != null) {
            String title = currentDifficulty.toUpperCase() + " LEVEL";
            tvDifficultyTitle.setText(title);
        }
    }
    
    /**
     * Display leaderboard using SharedPreferences (legacy method)
     */
    private void displayLeaderboard(SharedPreferences prefs, String difficulty, int textViewId) {
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Integer> bestScores = new HashMap<>();
        String currentUser = prefs.getString("current_user", "");
        
        // First pass: collect all scores
        for (String key : allEntries.keySet()) {
            if (key.startsWith("highscore_" + difficulty + "_") && !key.equals("highscore_" + difficulty + "_user")) {
                String username = key.substring(("highscore_" + difficulty + "_").length());
                int score = 0;
                try {
                    score = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                
                // Keep the highest score for each username
                if (!bestScores.containsKey(username) || score > bestScores.get(username)) {
                    bestScores.put(username, score);
                }
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userScores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bestScores.entrySet()) {
            userScores.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userScores, (a, b) -> Integer.compare(b.score, a.score));

        displayFormattedLeaderboard(userScores, textViewId);
    }
    
    /**
     * Display leaderboard using SQLite database
     */
    private void displayLeaderboardFromSQLite(String difficulty, int textViewId) {
        // Get top scores from the database
        List<Map<String, Object>> topScores = dataManager.getTopScores(difficulty, 5);
        
        // Convert to UserScore objects
        List<UserScore> userScores = new ArrayList<>();
        for (Map<String, Object> scoreData : topScores) {
            String username = (String) scoreData.get("username");
            int score = (int) scoreData.get("score");
            userScores.add(new UserScore(username, score));
        }
        
        // Display the formatted leaderboard
        displayFormattedLeaderboard(userScores, textViewId);
    }
    
    /**
     * Format and display the leaderboard with medals and rankings
     */
    private void displayFormattedLeaderboard(List<UserScore> userScores, int textViewId) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 Top Players:\n");
        int limit = Math.min(5, userScores.size());
        if (limit == 0) {
            sb.append("No scores yet. Be the first!");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userScores.get(i);
                String medal = "";
                if (i == 0) medal = "🥇 ";
                else if (i == 1) medal = "🥈 ";
                else if (i == 2) medal = "🥉 ";
                
                sb.append(medal + (i + 1) + ". " + us.username + " - " + us.score + "\n");
            }
        }

        ((TextView) findViewById(textViewId)).setText(sb.toString());
    }

    @Override
    protected void playBackButtonSound() {
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && backButtonPlayer != null) {
            backButtonPlayer.start();
        }
    }

    private static class UserScore {
        String username;
        int score;

        UserScore(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }

    @Override
    public void onBackPressed() {
        playBackButtonSound();
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backButtonPlayer != null) {
            backButtonPlayer.release();
            backButtonPlayer = null;
        }
    }
}
