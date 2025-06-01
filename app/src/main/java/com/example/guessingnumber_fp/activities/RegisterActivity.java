package com.example.guessingnumber_fp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.database.GameDataManager;
import com.example.guessingnumber_fp.database.UserDAO;
import com.example.guessingnumber_fp.activities.SoundManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends BaseActivity {
    private EditText etRegisterUsername, etRegisterPassword;
    private Button btnRegister, btnGoToLogin;
    private SharedPreferences prefs;
    private GameDataManager dataManager;
    private boolean isNavigatingWithinApp = false;
    private ImageView ivTogglePasswordRegister;
    private boolean isPasswordVisible = false;

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
        ivTogglePasswordRegister = findViewById(R.id.ivTogglePasswordRegister);

        startMenuMusic();

        ivTogglePasswordRegister.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePasswordRegister.setImageResource(R.drawable.ic_eye_open);
            } else {
                etRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePasswordRegister.setImageResource(R.drawable.ic_eye_closed);
            }
            etRegisterPassword.setSelection(etRegisterPassword.getText().length());
        });

        // Real-time validation
        etRegisterUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String username = s.toString();
                if (!username.matches("^[a-zA-Z0-9_]{4,16}$")) {
                    etRegisterUsername.setError("4-16 chars, letters/numbers/underscores only");
                } else {
                    etRegisterUsername.setError(null);
                }
            }
        });
        etRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (password.length() < 6) {
                    etRegisterPassword.setError("Password must be at least 6 characters");
                } else {
                    etRegisterPassword.setError(null);
                }
            }
        });

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
                if (etRegisterUsername.getError() != null || etRegisterPassword.getError() != null) {
                    Toast.makeText(RegisterActivity.this, "Please fix input errors", Toast.LENGTH_SHORT).show();
                    return;
                }
                String hashedPassword = sha256(password);

                if (GameDataManager.getStorageMode() == GameDataManager.MODE_SQLITE) {
                    UserDAO userDAO = new UserDAO(RegisterActivity.this);
                    if (userDAO.userExists(username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        userDAO.createUser(username);
                        dataManager.putString("password_" + username, hashedPassword);
                    }
                } else {
                    if (prefs.contains("password_" + username)) {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        prefs.edit().putString("password_" + username, hashedPassword).apply();
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