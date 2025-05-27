package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;

public class SettingsActivity extends BaseActivity {
    private Switch switchSound;
    private Button btnLogout, btnChangeUsername, btnChangePassword;
    private EditText etNewUsername, etNewPassword, etConfirmPassword;
    private SharedPreferences prefs;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        switchSound = findViewById(R.id.switchSound);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        
        // Get current user
        currentUser = prefs.getString("current_user", "");

        // Load saved settings
        boolean soundOn = prefs.getBoolean("sound_on", true);
        switchSound.setChecked(soundOn);

        // Music option removed, keeping only sound
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("sound_on", isChecked).apply();
            }
        });
        // Set up change username button
        btnChangeUsername.setOnClickListener(v -> {
            String newUsername = etNewUsername.getText().toString().trim();
            if (TextUtils.isEmpty(newUsername)) {
                Toast.makeText(this, "Please enter a new username", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if username already exists (except current user)
            if (!newUsername.equals(currentUser) && prefs.contains("password_" + newUsername)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get the current password
            String currentPassword = prefs.getString("password_" + currentUser, "");
            
            // Save the password with the new username
            prefs.edit().putString("password_" + newUsername, currentPassword).apply();
            
            // If the user is changing their own username, update current_user
            prefs.edit().putString("current_user", newUsername).apply();
            
            // Transfer game stats to new username
            transferGameStats(currentUser, newUsername);
            
            // Remove old username data if different
            if (!newUsername.equals(currentUser)) {
                prefs.edit().remove("password_" + currentUser).apply();
            }
            
            // Update current user reference
            currentUser = newUsername;
            
            // Clear the field
            etNewUsername.setText("");
            
            Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
        });
        
        // Set up change password button
        btnChangePassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Update password
            prefs.edit().putString("password_" + currentUser, newPassword).apply();
            
            // Clear fields
            etNewPassword.setText("");
            etConfirmPassword.setText("");
            
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove("current_user").apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // Add this for the back button
        android.widget.ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);
        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> {
                isNavigatingWithinApp = true;
                isInGameFlow = false;
                onBackPressed();
            });
        }
    }
    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;
        isInGameFlow = false;
        super.onBackPressed();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isSoundOn() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        return prefs.getBoolean("sound_on", true);
    }
    
    /**
     * Transfers game statistics from old username to new username
     */
    private void transferGameStats(String oldUsername, String newUsername) {
        // Transfer high scores and other game stats
        String[] difficulties = {"easy", "medium", "hard", "impossible"};
        
        for (String difficulty : difficulties) {
            // Transfer high score
            int highScore = prefs.getInt("highscore_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("highscore_" + difficulty + "_" + newUsername, highScore).apply();
            
            // Transfer games played
            int gamesPlayed = prefs.getInt("games_played_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("games_played_" + difficulty + "_" + newUsername, gamesPlayed).apply();
            
            // Transfer games won
            int gamesWon = prefs.getInt("games_won_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("games_won_" + difficulty + "_" + newUsername, gamesWon).apply();
            
            // Transfer games lost
            int gamesLost = prefs.getInt("games_lost_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("games_lost_" + difficulty + "_" + newUsername, gamesLost).apply();
            
            // Transfer hints used
            int hintsUsed = prefs.getInt("hints_used_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("hints_used_" + difficulty + "_" + newUsername, hintsUsed).apply();
            
            // Transfer hints
            int hints = prefs.getInt("hints_" + difficulty + "_" + oldUsername, 0);
            prefs.edit().putInt("hints_" + difficulty + "_" + newUsername, hints).apply();
            
            // Remove old stats if usernames are different
            if (!oldUsername.equals(newUsername)) {
                prefs.edit().remove("highscore_" + difficulty + "_" + oldUsername).apply();
                prefs.edit().remove("games_played_" + difficulty + "_" + oldUsername).apply();
                prefs.edit().remove("games_won_" + difficulty + "_" + oldUsername).apply();
                prefs.edit().remove("games_lost_" + difficulty + "_" + oldUsername).apply();
                prefs.edit().remove("hints_used_" + difficulty + "_" + oldUsername).apply();
                prefs.edit().remove("hints_" + difficulty + "_" + oldUsername).apply();
            }
            
            // Update global highscore user if needed
            String highscoreUser = prefs.getString("highscore_" + difficulty + "_user", "");
            if (highscoreUser.equals(oldUsername)) {
                prefs.edit().putString("highscore_" + difficulty + "_user", newUsername).apply();
            }
        }
    }
}
