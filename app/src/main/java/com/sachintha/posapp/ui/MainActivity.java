package com.sachintha.posapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.utils.FormatUtils;
import com.sachintha.posapp.utils.SessionManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Main Dashboard Activity
 * Displays statistics and quick access to all POS features
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserRole;
    private TextView tvTodaySales, tvTodayTransactions, tvTotalProducts, tvLowStock;
    private CardView cardPOS, cardProducts, cardSalesHistory, cardInventory, cardSettings;
    private LinearLayout btnLogout;

    private POSDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = POSApplication.getInstance().getDatabase();
        sessionManager = SessionManager.getInstance(this);

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserRole = findViewById(R.id.tv_user_role);
        tvTodaySales = findViewById(R.id.tv_today_sales);
        tvTodayTransactions = findViewById(R.id.tv_today_transactions);
        tvTotalProducts = findViewById(R.id.tv_total_products);
        tvLowStock = findViewById(R.id.tv_low_stock);

        cardPOS = findViewById(R.id.card_pos);
        cardProducts = findViewById(R.id.card_products);
        cardSalesHistory = findViewById(R.id.card_sales_history);
        cardInventory = findViewById(R.id.card_inventory);
        cardSettings = findViewById(R.id.card_settings);
        btnLogout = findViewById(R.id.btn_logout);

        // Set welcome message
        tvWelcome.setText("Welcome, " + sessionManager.getFullName() + "!");
        tvUserRole.setText(sessionManager.getRole());

        // Show/hide admin features
        if (!sessionManager.isAdmin()) {
            cardSettings.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        cardPOS.setOnClickListener(v -> {
            startActivity(new Intent(this, POSActivity.class));
        });

        cardProducts.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductActivity.class));
        });

        cardSalesHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, SalesHistoryActivity.class));
        });

        cardInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });

        cardSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadDashboardData() {
        // Get today's date range
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        // Load statistics
        double todaySales = database.saleDao().getTotalSalesBetweenDates(startOfDay, endOfDay);
        int todayTransactions = database.saleDao().getSalesCountBetweenDates(startOfDay, endOfDay);
        int totalProducts = database.productDao().getActiveProductCount();
        int lowStockCount = database.productDao().getLowStockCount();

        // Update UI
        tvTodaySales.setText(FormatUtils.formatCurrency(todaySales));
        tvTodayTransactions.setText(String.valueOf(todayTransactions));
        tvTotalProducts.setText(String.valueOf(totalProducts));
        tvLowStock.setText(String.valueOf(lowStockCount));
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
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
