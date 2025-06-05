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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.cardview.widget.CardView;
import android.os.Build;
import com.google.android.material.button.MaterialButton;

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
    private int currentView = 0; // 0: Top Players, 1: Hint Abusers, 2: Persistent Players, 3: Lucky Cat, 4: Black Kittens

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        // Make the app full screen with immersive mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);
        startMenuMusic();

        backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
        
        // Setup toggle button
        MaterialButton btnToggle = findViewById(R.id.btnToggleLeaderboard);
        btnToggle.setOnClickListener(v -> {
            currentView = (currentView + 1) % 5;
            switch (currentView) {
                case 0:
                    btnToggle.setText("Top Scorers");
                    break;
                case 1:
                    btnToggle.setText("Hint Abusers");
                    break;
                case 2:
                    btnToggle.setText("Persistent Players");
                    break;
                case 3:
                    btnToggle.setText("Lucky Kittens");
                    break;
                case 4:
                    btnToggle.setText("Black Kittens");
                    break;
            }
            updateLeaderboard();
        });
        
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
                updateLeaderboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Initial display
        updateDifficultyTitle();
        updateLeaderboard();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            playBackButtonSound();
            isNavigatingWithinApp = true;
            finish();
        });

        // Animate all cards and headers in
        int[] cardIds = new int[] {
            R.id.tvHighScoresTitle, R.id.btnToggleLeaderboard, R.id.spinnerHighscoreDifficulty, R.id.tvHighscoreContent
        };
        for (int i = 0; i < cardIds.length; i++) {
            View statView = findViewById(cardIds[i]);
            if (statView != null) {
                View card = statView;
                if (cardIds[i] == R.id.tvHighscoreContent) {
                    card = (View) statView.getParent().getParent(); // TextView -> LinearLayout -> CardView
                }
                card.setAlpha(0f);
                final View finalCard = card;
                card.postDelayed(() -> {
                    Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    finalCard.setAlpha(1f);
                    finalCard.startAnimation(slideIn);
                }, i * 100);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void updateDifficultyTitle() {
        TextView tvDifficultyTitle = findViewById(R.id.tvDifficultyTitle);
        if (tvDifficultyTitle != null) {
            String title = currentDifficulty.toUpperCase() + " LEVEL";
            tvDifficultyTitle.setText(title);
        }
    }
    
    private void updateLeaderboard() {
        if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
            switch (currentView) {
                case 0:
                    displayLeaderboardFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 1:
                    displayHintAbusersFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 2:
                    displayPersistentPlayersFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 3:
                    displayLuckyCatFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 4:
                    displayBlackKittensFromSQLite(currentDifficulty, R.id.tvHighscoreContent);
                    break;
            }
        } else {
            switch (currentView) {
                case 0:
                    displayLeaderboard(prefs, currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 1:
                    displayHintAbusers(prefs, currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 2:
                    displayPersistentPlayers(prefs, currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 3:
                    displayLuckyCat(prefs, currentDifficulty, R.id.tvHighscoreContent);
                    break;
                case 4:
                    displayBlackKittens(prefs, currentDifficulty, R.id.tvHighscoreContent);
                    break;
            }
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

    private void displayHintAbusers(SharedPreferences prefs, String difficulty, int textViewId) {
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Integer> hintCounts = new HashMap<>();
        
        // Collect all hint usage
        for (String key : allEntries.keySet()) {
            if (key.startsWith("hints_used_" + difficulty + "_")) {
                String username = key.substring(("hints_used_" + difficulty + "_").length());
                int hints = 0;
                try {
                    hints = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                
                if (hints > 0) {
                    hintCounts.put(username, hints);
                }
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userHints = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hintCounts.entrySet()) {
            userHints.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userHints, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedHintAbusers(userHints, textViewId);
    }

    private void displayHintAbusersFromSQLite(String difficulty, int textViewId) {
        Map<String, Integer> hintCounts = new HashMap<>();
        Map<String, Integer> stats = dataManager.getStatsForUser(dataManager.getCurrentUser(), difficulty);
        
        // Get all users and their hint usage
        List<String> users = dataManager.getAllUsers();
        for (String username : users) {
            Map<String, Integer> userStats = dataManager.getStatsForUser(username, difficulty);
            int hints = userStats.getOrDefault("hints_used", 0);
            if (hints > 0) {
                hintCounts.put(username, hints);
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userHints = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hintCounts.entrySet()) {
            userHints.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userHints, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedHintAbusers(userHints, textViewId);
    }

    private void displayFormattedHintAbusers(List<UserScore> userHints, int textViewId) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔍 Top Hint Users:\n");
        int limit = Math.min(5, userHints.size());
        if (limit == 0) {
            sb.append("No hint usage recorded yet!");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userHints.get(i);
                String medal = "";
                if (i == 0) medal = "🥇 ";
                else if (i == 1) medal = "🥈 ";
                else if (i == 2) medal = "🥉 ";
                
                sb.append(medal + (i + 1) + ". " + us.username + " - " + us.score + " hints\n");
            }
        }

        ((TextView) findViewById(textViewId)).setText(sb.toString());
    }

    private void displayPersistentPlayers(SharedPreferences prefs, String difficulty, int textViewId) {
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Integer> gamesPlayed = new HashMap<>();
        
        // Collect all games played
        for (String key : allEntries.keySet()) {
            if (key.startsWith("games_played_" + difficulty + "_")) {
                String username = key.substring(("games_played_" + difficulty + "_").length());
                int games = 0;
                try {
                    games = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                
                if (games > 0) {
                    gamesPlayed.put(username, games);
                }
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userGames = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesPlayed.entrySet()) {
            userGames.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userGames, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedPersistentPlayers(userGames, textViewId);
    }

    private void displayPersistentPlayersFromSQLite(String difficulty, int textViewId) {
        Map<String, Integer> gamesPlayed = new HashMap<>();
        
        // Get all users and their games played
        List<String> users = dataManager.getAllUsers();
        for (String username : users) {
            Map<String, Integer> userStats = dataManager.getStatsForUser(username, difficulty);
            int games = userStats.getOrDefault("games_played", 0);
            if (games > 0) {
                gamesPlayed.put(username, games);
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userGames = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesPlayed.entrySet()) {
            userGames.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userGames, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedPersistentPlayers(userGames, textViewId);
    }

    private void displayFormattedPersistentPlayers(List<UserScore> userGames, int textViewId) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎮 Most Games Played:\n");
        int limit = Math.min(5, userGames.size());
        if (limit == 0) {
            sb.append("No games played yet!");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userGames.get(i);
                String medal = "";
                if (i == 0) medal = "🥇 ";
                else if (i == 1) medal = "🥈 ";
                else if (i == 2) medal = "🥉 ";
                
                sb.append(medal + (i + 1) + ". " + us.username + " - " + us.score + " games\n");
            }
        }

        ((TextView) findViewById(textViewId)).setText(sb.toString());
    }

    private void displayLuckyCat(SharedPreferences prefs, String difficulty, int textViewId) {
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Integer> gamesWon = new HashMap<>();
        
        // Collect all games won
        for (String key : allEntries.keySet()) {
            if (key.startsWith("games_won_" + difficulty + "_")) {
                String username = key.substring(("games_won_" + difficulty + "_").length());
                int wins = 0;
                try {
                    wins = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                
                if (wins > 0) {
                    gamesWon.put(username, wins);
                }
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userWins = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesWon.entrySet()) {
            userWins.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userWins, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedLuckyCat(userWins, textViewId);
    }

    private void displayLuckyCatFromSQLite(String difficulty, int textViewId) {
        Map<String, Integer> gamesWon = new HashMap<>();
        
        // Get all users and their games won
        List<String> users = dataManager.getAllUsers();
        for (String username : users) {
            Map<String, Integer> userStats = dataManager.getStatsForUser(username, difficulty);
            int wins = userStats.getOrDefault("games_won", 0);
            if (wins > 0) {
                gamesWon.put(username, wins);
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userWins = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesWon.entrySet()) {
            userWins.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userWins, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedLuckyCat(userWins, textViewId);
    }

    private void displayFormattedLuckyCat(List<UserScore> userWins, int textViewId) {
        StringBuilder sb = new StringBuilder();
        sb.append("🐱 Lucky Cat:\n");
        int limit = Math.min(5, userWins.size());
        if (limit == 0) {
            sb.append("No wins recorded yet!");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userWins.get(i);
                String medal = "";
                if (i == 0) medal = "😺 ";
                else if (i == 1) medal = "😺 ";
                else if (i == 2) medal = "😺 ";
                
                sb.append(medal + (i + 1) + ". " + us.username + " - " + us.score + " wins\n");
            }
        }

        ((TextView) findViewById(textViewId)).setText(sb.toString());
    }

    private void displayBlackKittens(SharedPreferences prefs, String difficulty, int textViewId) {
        Map<String, ?> allEntries = prefs.getAll();
        Map<String, Integer> gamesLost = new HashMap<>();
        
        // Collect all games lost
        for (String key : allEntries.keySet()) {
            if (key.startsWith("games_lost_" + difficulty + "_")) {
                String username = key.substring(("games_lost_" + difficulty + "_").length());
                int losses = 0;
                try {
                    losses = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                
                if (losses > 0) {
                    gamesLost.put(username, losses);
                }
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userLosses = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesLost.entrySet()) {
            userLosses.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userLosses, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedBlackKittens(userLosses, textViewId);
    }

    private void displayBlackKittensFromSQLite(String difficulty, int textViewId) {
        Map<String, Integer> gamesLost = new HashMap<>();
        
        // Get all users and their games lost
        List<String> users = dataManager.getAllUsers();
        for (String username : users) {
            Map<String, Integer> userStats = dataManager.getStatsForUser(username, difficulty);
            int losses = userStats.getOrDefault("games_lost", 0);
            if (losses > 0) {
                gamesLost.put(username, losses);
            }
        }
        
        // Convert to list for sorting
        List<UserScore> userLosses = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gamesLost.entrySet()) {
            userLosses.add(new UserScore(entry.getKey(), entry.getValue()));
        }

        Collections.sort(userLosses, (a, b) -> Integer.compare(b.score, a.score));
        displayFormattedBlackKittens(userLosses, textViewId);
    }

    private void displayFormattedBlackKittens(List<UserScore> userLosses, int textViewId) {
        StringBuilder sb = new StringBuilder();
        sb.append("🐱 Black Kittens:\n");
        int limit = Math.min(5, userLosses.size());
        if (limit == 0) {
            sb.append("No losses recorded yet!");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userLosses.get(i);
                String medal = "";
                if (i == 0) medal = "😹 ";
                else if (i == 1) medal = "😹 ";
                else if (i == 2) medal = "😹 ";
                
                sb.append(medal + (i + 1) + ". " + us.username + " - " + us.score + " losses\n");
            }
        }

        ((TextView) findViewById(textViewId)).setText(sb.toString());
    }

    private void playBackButtonSound() {
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
        // Play back button sound
        SoundManager.playSound(this, R.raw.cat_back_btn);
        
        // Animate all cards and headers out
        int[] viewIds = new int[] {
            R.id.tvHighScoresTitle, R.id.btnToggleLeaderboard, R.id.spinnerHighscoreDifficulty, R.id.tvHighscoreContent
        };
        for (int id : viewIds) {
            View statView = findViewById(id);
            if (statView != null) {
                View card = statView;
                if (id == R.id.tvHighscoreContent) {
                    card = (View) statView.getParent().getParent();
                }
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
                card.startAnimation(slideOut);
            }
        }
        // Delay finish to allow animation
        new android.os.Handler().postDelayed(super::onBackPressed, 600);
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
