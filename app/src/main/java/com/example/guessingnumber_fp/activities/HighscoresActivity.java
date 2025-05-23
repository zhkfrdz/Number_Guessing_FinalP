package com.example.guessingnumber_fp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class HighscoresActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setStatusBarColor(0xFF000000);
        setContentView(R.layout.activity_highscores);

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        ((TextView) findViewById(R.id.tvHighscoreEasy)).setText("Easy: " + prefs.getInt("highscore_easy", 0));
        ((TextView) findViewById(R.id.tvHighscoreMedium)).setText("Medium: " + prefs.getInt("highscore_medium", 0));
        ((TextView) findViewById(R.id.tvHighscoreHard)).setText("Hard: " + prefs.getInt("highscore_hard", 0));

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
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
