package com.sachintha.posapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.User;
import com.sachintha.posapp.utils.SessionManager;

/**
 * Login Activity for user authentication
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvForgotPassword, tvVersion;

    private POSDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = POSApplication.getInstance().getDatabase();
        sessionManager = SessionManager.getInstance(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvVersion = findViewById(R.id.tv_version);

        tvVersion.setText("Version 1.0");
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Please contact your administrator", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        // Reset errors
        etUsername.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Show progress
        showProgress(true);

        // Authenticate user
        User user = database.userDao().authenticate(username, password);

        showProgress(false);

        if (user != null) {
            // Login successful
            sessionManager.createLoginSession(user);
            Toast.makeText(this, "Welcome, " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Move app to background instead of closing
        moveTaskToBack(true);
    }
}
