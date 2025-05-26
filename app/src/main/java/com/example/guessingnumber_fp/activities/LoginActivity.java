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

public class LoginActivity extends BaseActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPreferences prefs;

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
                // Save current user
                prefs.edit().putString("current_user", username).apply();
                // Optionally: Save password for demo (not secure for real apps)
                prefs.edit().putString("password_" + username, password).apply();
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
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean musicOn = prefs.getBoolean("music_on", true);
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
