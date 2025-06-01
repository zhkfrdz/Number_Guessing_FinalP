package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;
import com.example.guessingnumber_fp.database.UserDAO;
import com.example.guessingnumber_fp.activities.SoundManager;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.text.Editable;
import android.text.TextWatcher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends BaseActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    private boolean isNavigatingWithinApp = false;
    private ImageView ivTogglePasswordLogin;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);

        startMenuMusic();

        ivTogglePasswordLogin = findViewById(R.id.ivTogglePasswordLogin);
        ivTogglePasswordLogin.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePasswordLogin.setImageResource(R.drawable.ic_eye_open);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePasswordLogin.setImageResource(R.drawable.ic_eye_closed);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Real-time validation
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String username = s.toString();
                if (!username.matches("^[a-zA-Z0-9_]{4,16}$")) {
                    etUsername.setError("4-16 chars, letters/numbers/underscores only");
                } else {
                    etUsername.setError(null);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (password.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters");
                } else {
                    etPassword.setError(null);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(LoginActivity.this, R.raw.cat_buttons);
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etUsername.getError() != null || etPassword.getError() != null) {
                    Toast.makeText(LoginActivity.this, "Please fix input errors", Toast.LENGTH_SHORT).show();
                    return;
                }
                String hashedPassword = sha256(password);
                
                // Check if we're using SQLite or SharedPreferences
                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    // Use UserDAO for authentication
                    UserDAO userDAO = new UserDAO(LoginActivity.this);
                    
                    // Check if user exists
                    if (!userDAO.userExists(username)) {
                        // Create new user if not exists
                        userDAO.createUser(username);
                        // Save password
                        dataManager.putString("password_" + username, hashedPassword);
                    } else {
                        // Verify password
                        String savedPassword = dataManager.getString("password_" + username, "");
                        if (!hashedPassword.equals(savedPassword)) {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    // Legacy SharedPreferences approach
                    // Save password for this user
                    prefs.edit().putString("password_" + username, hashedPassword).apply();
                }
                
                // Save current user using GameDataManager
                dataManager.setCurrentUser(username);
                
                // Go to MainActivity
                isNavigatingWithinApp = true;
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(LoginActivity.this, R.raw.cat_buttons);
                isNavigatingWithinApp = true;
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp) {
            MusicManager.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNavigatingWithinApp = false;
        
        // Use GameDataManager to check music settings
        boolean musicOn = dataManager.isMusicEnabled();
        if (musicOn) {
            MusicManager.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isNavigatingWithinApp) {
            MusicManager.release();
        }
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
}
