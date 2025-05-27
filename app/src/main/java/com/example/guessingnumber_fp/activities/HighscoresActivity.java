package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.guessingnumber_fp.R;

public class HighscoresActivity extends BaseActivity {

    private MediaPlayer backButtonPlayer;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        startMenuMusic();

        backButtonPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);

        displayLeaderboard(prefs, "easy", R.id.tvHighscoreEasy);
        displayLeaderboard(prefs, "medium", R.id.tvHighscoreMedium);
        displayLeaderboard(prefs, "hard", R.id.tvHighscoreHard);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            playBackButtonSound();
            isNavigatingWithinApp = true;
            finish();
        });
    }

    private void displayLeaderboard(SharedPreferences prefs, String difficulty, int textViewId) {
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

        java.util.Collections.sort(userScores, (a, b) -> Integer.compare(b.score, a.score));

        StringBuilder sb = new StringBuilder();
        sb.append("Top Players:\n");
        int limit = Math.min(5, userScores.size());
        if (limit == 0) {
            sb.append("No scores yet.");
        } else {
            for (int i = 0; i < limit; i++) {
                UserScore us = userScores.get(i);
                sb.append((i + 1) + ". " + us.username + " - " + us.score + "\n");
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
