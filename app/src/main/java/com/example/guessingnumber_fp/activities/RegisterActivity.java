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
import com.example.guessingnumber_fp.activities.SoundManager;

public class RegisterActivity extends BaseActivity {
    private EditText etRegisterUsername, etRegisterPassword;
    private Button btnRegister, btnGoToLogin;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    private boolean isNavigatingWithinApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(0xFF000000);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        dataManager = GameDataManager.getInstance(this);

        startMenuMusic();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(RegisterActivity.this, R.raw.cat_buttons);
                String username = etRegisterUsername.getText().toString().trim();
                String password = etRegisterPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    UserDAO userDAO = new UserDAO(RegisterActivity.this);
                    if (userDAO.userExists(username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        userDAO.createUser(username);
                        dataManager.putString("password_" + username, password);
                    }
                } else {
                    if (prefs.contains("password_" + username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        prefs.edit().putString("password_" + username, password).apply();
                    }
                }

                isNavigatingWithinApp = true;
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(RegisterActivity.this, R.raw.cat_buttons);
                isNavigatingWithinApp = true;
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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