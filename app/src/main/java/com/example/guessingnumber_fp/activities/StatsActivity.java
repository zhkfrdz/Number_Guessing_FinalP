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

        findViewById(R.id.btnResetStats).setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("games_played_" + difficulty, 0);
            editor.putInt("games_won_" + difficulty, 0);
            editor.putInt("games_lost_" + difficulty, 0);
            editor.putInt("hints_used_" + difficulty, 0);
            editor.putInt("highscore_" + difficulty, 0);
            editor.putInt("total_score_" + difficulty, 0); // if used
            editor.apply();
            updateStats();
            Toast.makeText(this, "Stats reset for " + difficulty, Toast.LENGTH_SHORT).show();
        });

    }

    private void updateStats() {
        int gamesPlayed = prefs.getInt("games_played_" + difficulty, 0);
        int gamesWon = prefs.getInt("games_won_" + difficulty, 0);
        int gamesLost = prefs.getInt("games_lost_" + difficulty, 0);
        int hintsUsed = prefs.getInt("hints_used_" + difficulty, 0);
        int bestScore = prefs.getInt("highscore_" + difficulty, 0);

        ((TextView)findViewById(R.id.tvGamesPlayed)).setText(String.valueOf(gamesPlayed));
        ((TextView)findViewById(R.id.tvGamesWon)).setText(String.valueOf(gamesWon));
        ((TextView)findViewById(R.id.tvGamesLost)).setText(String.valueOf(gamesLost));
        ((TextView)findViewById(R.id.tvHintsUsed)).setText(String.valueOf(hintsUsed));
        ((TextView)findViewById(R.id.tvBestScore)).setText(String.valueOf(bestScore));

        // Calculate win rate
        int winRate = gamesPlayed > 0 ? (gamesWon * 100) / gamesPlayed : 0;
        ((TextView)findViewById(R.id.tvWinRate)).setText(winRate + "%");
        int totalScore = prefs.getInt("total_score_" + difficulty, 0);
        int avgScore = gamesPlayed > 0 ? totalScore / gamesPlayed : 0;
        ((TextView)findViewById(R.id.tvAverageScore)).setText(String.valueOf(avgScore));


    }
    @Override
    protected void onPause() {
        super.onPause();
        // No music control here
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No music control here
    }


}