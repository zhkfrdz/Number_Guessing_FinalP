package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;
import com.example.guessingnumber_fp.database.DatabaseMigrationHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SettingsActivity extends BaseActivity {
    private Switch switchMusic, switchSound;
    private Button btnLogout, btnSaveChanges;
    private EditText etNewUsername, etCurrentPassword, etNewPassword;
    private ImageView ivToggleCurrentPassword, ivToggleNewPassword;
    private boolean isCurrentPasswordVisible = false, isNewPasswordVisible = false;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    // No need for MediaPlayer instances with global SoundManager

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
        ivToggleCurrentPassword = findViewById(R.id.ivToggleCurrentPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);

        // Use GameDataManager to get settings
        boolean musicOn = dataManager.isMusicEnabled();
        boolean soundOn = dataManager.isSoundEnabled();
        switchMusic.setChecked(musicOn);
        switchSound.setChecked(soundOn);

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playToggleSound();
            dataManager.setMusicEnabled(isChecked);
            if (isChecked) {
                startMenuMusic();
            } else {
                MusicManager.stop();
            }
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playToggleSound();
            dataManager.setSoundEnabled(isChecked);
        });

        // Get current username to display as hint
        String currentUser = dataManager.getCurrentUser();
        etNewUsername.setHint("New Username (current: " + currentUser + ")");
        
        btnSaveChanges.setOnClickListener(v -> {
            playButtonClickSound();
            saveChanges();
        });
        
        btnLogout.setOnClickListener(v -> {
            playButtonClickSound(); // 🔊 Standard click sound
            // Use GameDataManager to handle logout
            dataManager.remove("current_user"); // Remove current user key
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

        ivToggleCurrentPassword.setOnClickListener(v -> {
            playToggleSound();
            isCurrentPasswordVisible = !isCurrentPasswordVisible;
            if (isCurrentPasswordVisible) {
                etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleCurrentPassword.setImageResource(R.drawable.show);
            } else {
                etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleCurrentPassword.setImageResource(R.drawable.hide);
            }
            etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        });
        ivToggleNewPassword.setOnClickListener(v -> {
            playToggleSound();
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleNewPassword.setImageResource(R.drawable.show);
            } else {
                etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleNewPassword.setImageResource(R.drawable.hide);
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });
        // Real-time validation
        etNewUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String username = s.toString();
                if (!username.isEmpty() && !username.matches("^[a-zA-Z0-9_]{4,16}$")) {
                    etNewUsername.setError("4-16 chars, letters/numbers/underscores only");
                } else {
                    etNewUsername.setError(null);
                }
            }
        });
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (!password.isEmpty() && password.length() < 6) {
                    etNewPassword.setError("Password must be at least 6 characters");
                } else {
                    etNewPassword.setError(null);
                }
            }
        });

        // Sequential fade-in animation for all elements
        final long fadeDuration = 180;
        final long fadeDelay = 60;
        // Find all views in order
        TextView tvTitle = null;
        for (int i = 0; i < ((android.view.ViewGroup) findViewById(android.R.id.content)).getChildCount(); i++) {
            View v = ((android.view.ViewGroup) findViewById(android.R.id.content)).getChildAt(i);
            if (v instanceof android.widget.TextView && ((TextView) v).getText().toString().equals("Settings")) {
                tvTitle = (TextView) v;
                break;
            }
        }
        if (tvTitle == null) {
            // fallback: find by traversing the layout
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            tvTitle = findTextViewWithText(root, "Settings");
        }
        View musicRow = ((ViewGroup) switchMusic.getParent());
        View soundRow = ((ViewGroup) switchSound.getParent());
        TextView tvChangeUsername = findTextViewWithText((ViewGroup) findViewById(android.R.id.content), "Change Username");
        TextView tvChangePassword = findTextViewWithText((ViewGroup) findViewById(android.R.id.content), "Change Password");
        // Prepare all views for fade-in
        View[] fadeViews = new View[] {
            tvTitle, btnBackSettings, musicRow, soundRow, tvChangeUsername, etNewUsername, tvChangePassword, etCurrentPassword, ivToggleCurrentPassword, etNewPassword, ivToggleNewPassword, btnSaveChanges, btnLogout
        };
        for (View v : fadeViews) {
            if (v != null) v.setAlpha(0f);
        }
        // Animate sequentially
        for (int i = 0; i < fadeViews.length; i++) {
            final View v = fadeViews[i];
            if (v != null) {
                v.postDelayed(() -> v.animate().alpha(1f).setDuration(fadeDuration).start(), i * fadeDelay);
            }
        }
    }

    private void playButtonClickSound() {
        // Use global SoundManager to play button click sound
        SoundManager.playSound(this, R.raw.cat_buttons);
    }

    private void playBackButtonClickSound() {
        // Use global SoundManager to play back button sound
        SoundManager.playSound(this, R.raw.cat_back_btn);
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
        // No need to release MediaPlayer instances with global SoundManager
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
        String currentUser = dataManager.getCurrentUser();
        String newUsername = etNewUsername.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        
        // Check if current password is correct
        String savedPassword = "";
        
        // Try to get password from database if using SQLite
        if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
            savedPassword = dataManager.getString("password_" + currentUser, "");
        } else {
            savedPassword = prefs.getString("password_" + currentUser, "");
        }
        
        // If trying to change password
        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword)) {
            String hashedCurrentPassword = sha256(currentPassword);
            String hashedNewPassword = sha256(newPassword);
            
            if (!savedPassword.equals(hashedCurrentPassword)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (etNewPassword.getError() != null) {
                Toast.makeText(this, "Please fix password error", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save new password
            if (!TextUtils.isEmpty(newUsername)) {
                dataManager.putString("password_" + newUsername, hashedNewPassword);
            } else {
                dataManager.putString("password_" + currentUser, hashedNewPassword);
            }
        }
        
        // If trying to change username
        if (!TextUtils.isEmpty(newUsername) && !newUsername.equals(currentUser)) {
            if (etNewUsername.getError() != null) {
                Toast.makeText(this, "Please fix username error", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if username already exists (except current user)
            boolean usernameExists = false;
            
            if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                // Check in SQLite
                usernameExists = dataManager.contains("password_" + newUsername);
            } else {
                // Check in SharedPreferences
                usernameExists = prefs.contains("password_" + newUsername);
            }
            
            if (usernameExists) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Transfer all game data from old username to new username
            if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                // Use DatabaseMigrationHelper for SQLite transfers
                DatabaseMigrationHelper migrationHelper = new DatabaseMigrationHelper(this);
                migrationHelper.transferUserData(currentUser, newUsername);
            } else {
                // Use the original method for SharedPreferences
                transferUserData(currentUser, newUsername);
            }
            
            // Update username
            dataManager.setCurrentUser(newUsername);
            
            // If not changing password, copy current password to new username
            if (TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(savedPassword)) {
                dataManager.putString("password_" + newUsername, savedPassword);
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

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void playToggleSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.toggle);
        if (mp != null) {
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
        }
    }

    // Helper method to find TextView by text
    private TextView findTextViewWithText(ViewGroup root, String text) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView && text.equals(((TextView) v).getText().toString())) {
                return (TextView) v;
            } else if (v instanceof ViewGroup) {
                TextView result = findTextViewWithText((ViewGroup) v, text);
                if (result != null) return result;
            }
        }
        return null;
    }
}
