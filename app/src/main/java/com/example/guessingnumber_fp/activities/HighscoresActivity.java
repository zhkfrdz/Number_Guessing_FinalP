package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.guessingnumber_fp.R;

public class HighscoresActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        startMenuMusic();

        displayLeaderboard(prefs, "easy", R.id.tvHighscoreEasy);
        displayLeaderboard(prefs, "medium", R.id.tvHighscoreMedium);
        displayLeaderboard(prefs, "hard", R.id.tvHighscoreHard);

        ((ImageButton)findViewById(R.id.btnBack)).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            finish();
        });
    }

    private void displayLeaderboard(SharedPreferences prefs, String difficulty, int textViewId) {
        // Gather all user scores for this difficulty
        java.util.Map<String, ?> allEntries = prefs.getAll();
        java.util.List<UserScore> userScores = new java.util.ArrayList<>();
        for (String key : allEntries.keySet()) {
            if (key.startsWith("highscore_" + difficulty + "_") && !key.equals("highscore_" + difficulty + "_user")) {
                String username = key.substring(("highscore_" + difficulty + "_").length());
                int score = 0;
                try {
                    score = Integer.parseInt(allEntries.get(key).toString());
                } catch (Exception ignored) {}
                userScores.add(new UserScore(username, score));
            }
        }
        // Sort descending by score
        java.util.Collections.sort(userScores, (a, b) -> Integer.compare(b.score, a.score));
        // Build leaderboard string
        StringBuilder sb = new StringBuilder();
        sb.append("Top Players:\n");
        int limit = Math.min(5, userScores.size());
        if (limit == 0) {
            sb.append("No scores yet.");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userScores.get(i);
                sb.append((i+1) + ". " + us.username + " - " + us.score + "\n");
            }
        }
        ((TextView)findViewById(textViewId)).setText(sb.toString());
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
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }

}