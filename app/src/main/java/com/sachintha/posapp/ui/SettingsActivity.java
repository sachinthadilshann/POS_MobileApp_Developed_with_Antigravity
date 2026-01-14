package com.sachintha.posapp.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.utils.SessionManager;

/**
 * Settings Activity
 * Admin settings for the POS application
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CardView cardManageUsers, cardManageCategories, cardBackup, cardResetData, cardAbout;
    private TextView tvVersion, tvUserInfo;

    private POSDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        database = POSApplication.getInstance().getDatabase();
        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        cardManageUsers = findViewById(R.id.card_manage_users);
        cardManageCategories = findViewById(R.id.card_manage_categories);
        cardBackup = findViewById(R.id.card_backup);
        cardResetData = findViewById(R.id.card_reset_data);
        cardAbout = findViewById(R.id.card_about);
        tvVersion = findViewById(R.id.tv_version);
        tvUserInfo = findViewById(R.id.tv_user_info);

        tvVersion.setText("Version 1.0.0");
        tvUserInfo.setText("Logged in as: " + sessionManager.getFullName() + " (" + sessionManager.getRole() + ")");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        cardManageUsers.setOnClickListener(v -> {
            Toast.makeText(this, "User management coming soon", Toast.LENGTH_SHORT).show();
        });

        cardManageCategories.setOnClickListener(v -> {
            Toast.makeText(this, "Category management coming soon", Toast.LENGTH_SHORT).show();
        });

        cardBackup.setOnClickListener(v -> {
            Toast.makeText(this, "Backup feature coming soon", Toast.LENGTH_SHORT).show();
        });

        cardResetData.setOnClickListener(v -> showResetDataDialog());

        cardAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void showResetDataDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Reset All Data")
            .setMessage("⚠️ WARNING: This will permanently delete all sales, products, and user data. This action cannot be undone.\n\nAre you sure you want to continue?")
            .setPositiveButton("Reset Everything", (dialog, which) -> {
                // Show confirmation dialog
                new AlertDialog.Builder(this)
                    .setTitle("Final Confirmation")
                    .setMessage("Type 'RESET' to confirm data reset")
                    .setPositiveButton("I Understand", (d, w) -> {
                        // In a real app, you'd verify the user typed 'RESET'
                        Toast.makeText(this, "Data reset is disabled for safety", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Supermarket POS")
            .setMessage("Version 1.0.0\n\n" +
                    "A complete Point of Sale solution for supermarkets.\n\n" +
                    "Features:\n" +
                    "• Fast checkout with barcode scanning\n" +
                    "• Product & inventory management\n" +
                    "• Sales tracking & reporting\n" +
                    "• Multi-user support\n" +
                    "• Receipt printing\n\n" +
                    "© 2024 POS Solutions")
            .setPositiveButton("OK", null)
            .show();
    }
}
