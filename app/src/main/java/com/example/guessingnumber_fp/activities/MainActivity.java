package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer; // ✅ ADDED
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.view.ViewTreeObserver;
import com.example.guessingnumber_fp.R;
import android.app.AlertDialog;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.guessingnumber_fp.fragments.MainMenuFragment;
import com.example.guessingnumber_fp.fragments.SelectDifficultyFragment;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.Gravity;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {
    private boolean isQuitting = false;
    // No need for MediaPlayer instances with global SoundManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make the app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Set immersive mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean showSelectDifficulty = intent != null && intent.getBooleanExtra("show_select_difficulty", false);

        if (savedInstanceState == null) {
            if (showSelectDifficulty) {
                showSelectDifficultyFragment();
            } else {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MainMenuFragment())
                    .commit();
            }
        }

        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        String currentUser = prefs.getString("current_user", null);
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        isInGameFlow = false;
    }

    private void setupAnimatedButton(int buttonId, int colorFrom, int colorTo, Runnable onClickAction) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
            colorAnimation.setDuration(350);
            colorAnimation.addUpdateListener(animator -> {
                button.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
            });
            colorAnimation.start();
            onClickAction.run();
        });
    }

    private void playButtonClickSound() {
        // Use global SoundManager to play button click sound
        SoundManager.playSound(this, R.raw.cat_buttons);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to release MediaPlayer instances with global SoundManager
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
        
        isNavigatingWithinApp = false;
    }

    @Override
    public void onBackPressed() {
        // Play back button sound using global SoundManager
        SoundManager.playSound(this, R.raw.cat_back_btn);
        
        // Check if we're in SelectDifficultyFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SelectDifficultyFragment) {
            // If we're in SelectDifficultyFragment, go back to MainMenuFragment
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MainMenuFragment())
                .commit();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there's a back stack, pop it
            getSupportFragmentManager().popBackStack();
        } else {
            // If no back stack, show quit dialog
            showQuitDialog();
        }
    }

    public void showQuitDialog() {
        // Play quit sound using global SoundManager
        SoundManager.playSound(this, R.raw.quit_st);
        isQuitting = true;

        // Create a custom dialog with polished UI
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom view for the dialog
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(48, 35, 48, 48);
        dialogLayout.setGravity(Gravity.CENTER);
        dialogLayout.setBackgroundResource(R.drawable.dialog_gradient_bg);

        // Game over image
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.gameo);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        imgParams.gravity = Gravity.CENTER;
        imgParams.setMargins(0, 0, 0, 24);
        image.setLayoutParams(imgParams);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        dialogLayout.addView(image);

        // Title with styling
        TextView title = new TextView(this);
        title.setText("QUIT GAME?");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.audiowide));
        title.setTextColor(0xFFFF6B4A);
        title.setPadding(0, 0, 0, 24);
        dialogLayout.addView(title);

        // Message
        TextView message = new TextView(this);
        message.setText("Are you sure you want to quit?");
        message.setTextSize(18);
        message.setGravity(Gravity.CENTER);
        message.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins_medium));
        message.setTextColor(0xFFFFFFFF);
        message.setPadding(0, 0, 0, 32);
        dialogLayout.addView(message);

        // Button container
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER);

        // Yes button
        Button yesButton = new Button(this);
        yesButton.setText("YES");
        yesButton.setTextColor(0xFFFFFFFF);
        yesButton.setBackgroundResource(R.drawable.rounded_red_button);
        LinearLayout.LayoutParams yesParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        yesParams.setMargins(8, 0, 8, 0);
        yesButton.setLayoutParams(yesParams);

        // No button
        Button noButton = new Button(this);
        noButton.setText("NO");
        noButton.setTextColor(0xFFFFFFFF);
        noButton.setBackgroundResource(R.drawable.rounded_red_button);
        LinearLayout.LayoutParams noParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        noParams.setMargins(8, 0, 8, 0);
        noButton.setLayoutParams(noParams);

        buttonContainer.addView(yesButton);
        buttonContainer.addView(noButton);
        dialogLayout.addView(buttonContainer);

        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();

        // Set transparent background for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Button click handlers
        yesButton.setOnClickListener(v -> {
            // Play give up sound for Yes
            if (isSoundOn()) {
                SoundManager.playSound(this, R.raw.giveup);
            }
            MusicManager.release();
            finishAffinity();
        });

        noButton.setOnClickListener(v -> {
            // Play back button sound for No
            SoundManager.playSound(this, R.raw.cat_back_btn);
            isQuitting = false;
            dialog.dismiss();
        });

        dialog.show();
    }

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp && !isQuitting) {
            MusicManager.pause();
        }
    }

    public void showSelectDifficultyFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container, new SelectDifficultyFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMainMenuFragment() {
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, new MainMenuFragment())
            .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra("show_select_difficulty", false)) {
            showSelectDifficultyFragment();
        }
    }
}
