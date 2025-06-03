package com.example.guessingnumber_fp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.fragments.SelectDifficultyFragment;

public class SelectDifficultyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SelectDifficultyFragment())
                .commit();
        }
    }
} 