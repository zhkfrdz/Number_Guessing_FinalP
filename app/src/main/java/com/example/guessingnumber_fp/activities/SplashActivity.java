package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.app_name);

        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Animation drop = AnimationUtils.loadAnimation(this, R.anim.drop);

        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Only start MainActivity after both animations end
                checkAndStartMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        drop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Only start MainActivity after both animations end
                checkAndStartMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        logo.startAnimation(bounce);
        appName.startAnimation(drop);
    }

    private int animationsEnded = 0;

    private void checkAndStartMainActivity() {
        animationsEnded++;
        if (animationsEnded == 2) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500); // 1.5 seconds delay after animations
        }
    }
} 