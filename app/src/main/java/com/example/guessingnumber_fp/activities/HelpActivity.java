package com.example.guessingnumber_fp.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.guessingnumber_fp.R;

public class HelpActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        ImageButton btnBack = findViewById(R.id.btnBackHelp);
        btnBack.setOnClickListener(v -> {
            MediaPlayer backBtnPlayer = MediaPlayer.create(this, R.raw.cat_back_btn);
            if (backBtnPlayer != null) {
                backBtnPlayer.setLooping(false);
                backBtnPlayer.setOnCompletionListener(MediaPlayer::release);
                backBtnPlayer.start();
            }
            finish();
        });

        LinearLayout helpCard = findViewById(R.id.helpCard);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        helpCard.startAnimation(slideUp);
    }
} 