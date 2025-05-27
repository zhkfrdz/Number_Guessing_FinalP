package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;
import com.example.guessingnumber_fp.database.UserDAO;

public class LoginActivity extends BaseActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    private boolean isNavigatingWithinApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);

        startMenuMusic();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Check if we're using SQLite or SharedPreferences
                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    // Use UserDAO for authentication
                    UserDAO userDAO = new UserDAO(LoginActivity.this);
                    
                    // Check if user exists
                    if (!userDAO.userExists(username)) {
                        // Create new user if not exists
                        userDAO.createUser(username);
                        // Save password
                        dataManager.putString("password_" + username, password);
                    } else {
                        // Verify password
                        String savedPassword = dataManager.getString("password_" + username, "");
                        if (!password.equals(savedPassword)) {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    // Legacy SharedPreferences approach
                    // Save password for this user
                    prefs.edit().putString("password_" + username, password).apply();
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
}
