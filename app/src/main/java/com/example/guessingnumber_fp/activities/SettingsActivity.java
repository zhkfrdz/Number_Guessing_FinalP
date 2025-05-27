package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class SettingsActivity extends BaseActivity {
    private Switch switchMusic, switchSound;
    private Button btnLogout, btnSaveChanges;
    private EditText etNewUsername, etCurrentPassword, etNewPassword;
    private SharedPreferences prefs;
    private MediaPlayer buttonClickPlayer;
    private MediaPlayer backButtonClickPlayer; // 🎵 ADDED for back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        switchMusic = findViewById(R.id.switchMusic);
        switchSound = findViewById(R.id.switchSound);
        btnLogout = findViewById(R.id.btnLogout);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        etNewUsername = findViewById(R.id.etNewUsername);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        buttonClickPlayer = MediaPlayer.create(this, R.raw.cat_buttons);
        backButtonClickPlayer = MediaPlayer.create(this, R.raw.cat_back_btn); // 🎵 INIT

        boolean musicOn = prefs.getBoolean("music_on", true);
        boolean soundOn = prefs.getBoolean("sound_on", true);
        switchMusic.setChecked(musicOn);
        switchSound.setChecked(soundOn);

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("music_on", isChecked).apply();
            if (isChecked) {
                startMenuMusic();
            } else {
                MusicManager.stop();
            }
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_on", isChecked).apply();
        });

        // Get current username to display as hint
        String currentUser = prefs.getString("current_user", "");
        etNewUsername.setHint("New Username (current: " + currentUser + ")");
        
        btnSaveChanges.setOnClickListener(v -> {
            playButtonClickSound();
            saveChanges();
        });
        
        btnLogout.setOnClickListener(v -> {
            playButtonClickSound(); // 🔊 Standard click sound
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });

        ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);
        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> {
                playBackButtonClickSound(); // 🔊 Back button sound
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                onBackPressed();
            });
        }
    }

    private void playButtonClickSound() {
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && buttonClickPlayer != null) {
            buttonClickPlayer.start();
        }
    }

    private void playBackButtonClickSound() { // 🎵 NEW METHOD
        boolean soundOn = prefs.getBoolean("sound_on", true);
        if (soundOn && backButtonClickPlayer != null) {
            backButtonClickPlayer.start();
        }
    }

    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonClickPlayer != null) {
            buttonClickPlayer.release();
            buttonClickPlayer = null;
        }
        if (backButtonClickPlayer != null) {
            backButtonClickPlayer.release();
            backButtonClickPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
    }
    
    private void transferUserData(String oldUsername, String newUsername) {
        SharedPreferences.Editor editor = prefs.edit();
        String[] difficulties = {"easy", "medium", "hard", "impossible"};
        
        for (String difficulty : difficulties) {
            // Transfer highscores
            int highscore = prefs.getInt("highscore_" + difficulty + "_" + oldUsername, 0);
            if (highscore > 0) {
                editor.putInt("highscore_" + difficulty + "_" + newUsername, highscore);
                // Remove old username's highscore
                editor.remove("highscore_" + difficulty + "_" + oldUsername);
                
                // Update global highscore if this user has the global highscore
                String highscoreUser = prefs.getString("highscore_" + difficulty + "_user", "");
                if (oldUsername.equals(highscoreUser)) {
                    editor.putString("highscore_" + difficulty + "_user", newUsername);
                }
            }
            
            // Transfer hints
            int hints = prefs.getInt("hints_" + difficulty + "_" + oldUsername, 0);
            if (hints > 0) {
                editor.putInt("hints_" + difficulty + "_" + newUsername, hints);
                // Remove old username's hints
                editor.remove("hints_" + difficulty + "_" + oldUsername);
            }
            
            // Transfer games won
            int gamesWon = prefs.getInt("games_won_" + difficulty + "_" + oldUsername, 0);
            if (gamesWon > 0) {
                editor.putInt("games_won_" + difficulty + "_" + newUsername, gamesWon);
                // Remove old username's games won
                editor.remove("games_won_" + difficulty + "_" + oldUsername);
            }
            
            // Transfer games lost
            int gamesLost = prefs.getInt("games_lost_" + difficulty + "_" + oldUsername, 0);
            if (gamesLost > 0) {
                editor.putInt("games_lost_" + difficulty + "_" + newUsername, gamesLost);
                // Remove old username's games lost
                editor.remove("games_lost_" + difficulty + "_" + oldUsername);
            }
        }
        
        // Apply all changes at once
        editor.apply();
    }
    
    private void saveChanges() {
        String currentUser = prefs.getString("current_user", "");
        String newUsername = etNewUsername.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        
        // Check if current password is correct
        String savedPassword = prefs.getString("password_" + currentUser, "");
        
        // If trying to change password
        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword)) {
            if (!savedPassword.equals(currentPassword)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save new password
            if (!TextUtils.isEmpty(newUsername)) {
                // If username is also changing, save password under new username
                prefs.edit().putString("password_" + newUsername, newPassword).apply();
            } else {
                // Otherwise update password for current username
                prefs.edit().putString("password_" + currentUser, newPassword).apply();
            }
        }
        
        // If trying to change username
        if (!TextUtils.isEmpty(newUsername) && !newUsername.equals(currentUser)) {
            // Check if username already exists (except current user)
            if (prefs.contains("password_" + newUsername)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Transfer all game data from old username to new username
            transferUserData(currentUser, newUsername);
            
            // Update username
            prefs.edit().putString("current_user", newUsername).apply();
            
            // If not changing password, copy current password to new username
            if (TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(savedPassword)) {
                prefs.edit().putString("password_" + newUsername, savedPassword).apply();
            }
            
            // Update hint
            etNewUsername.setHint("New Username (current: " + newUsername + ")");
        }
        
        // Clear fields
        etNewUsername.setText("");
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        
        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
    }
}
