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

public class StatsActivity extends BaseActivity {
    private String currentDifficulty = "easy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", "guest");

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
                String[] difficulties = {"easy", "medium", "hard"};
                currentDifficulty = difficulties[position];
                displayStats(prefs, currentUser, currentDifficulty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial stats display
        displayStats(prefs, currentUser, currentDifficulty);

        ((ImageButton)findViewById(R.id.btnBackStats)).setOnClickListener(v -> {
            isNavigatingWithinApp = true;
            finish();
        });
    }

    private void displayStats(SharedPreferences prefs, String user, String difficulty) {
        int gamesPlayed = prefs.getInt("games_played_" + difficulty + "_" + user, 0);
        int gamesWon = prefs.getInt("games_won_" + difficulty + "_" + user, 0);
        int gamesLost = prefs.getInt("games_lost_" + difficulty + "_" + user, 0);
        int hintsUsed = prefs.getInt("hints_used_" + difficulty + "_" + user, 0);
        int bestScore = prefs.getInt("highscore_" + difficulty + "_" + user, 0);

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
}