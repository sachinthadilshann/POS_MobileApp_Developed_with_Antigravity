package com.sachintha.posapp.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.adapter.InventoryAdapter;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Management Activity
 * Shows stock levels with low stock alerts
 */
public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnInventoryActionListener {

    private ImageButton btnBack;
    private TabLayout tabLayout;
    private EditText etSearch;
    private RecyclerView rvInventory;
    private TextView tvEmpty, tvSummary;

    private POSDatabase database;
    private InventoryAdapter adapter;
    private List<Product> allProducts;
    private String currentFilter = "all"; // all, low, out

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        database = POSApplication.getInstance().getDatabase();

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tabLayout = findViewById(R.id.tab_layout);
        etSearch = findViewById(R.id.et_search);
        rvInventory = findViewById(R.id.rv_inventory);
        tvEmpty = findViewById(R.id.tv_empty);
        tvSummary = findViewById(R.id.tv_summary);

        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this, new ArrayList<>(), this);
        rvInventory.setAdapter(adapter);

        // Setup tabs
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Low Stock"));
        tabLayout.addTab(tabLayout.newTab().setText("Out of Stock"));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFilter = "all";
                        break;
                    case 1:
                        currentFilter = "low";
                        break;
                    case 2:
                        currentFilter = "out";
                        break;
                }
                filterInventory(etSearch.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterInventory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadInventory() {
        allProducts = database.productDao().getAllActiveProducts();
        updateSummary();
        filterInventory(etSearch.getText().toString());
    }

    private void updateSummary() {
        int total = allProducts.size();
        int lowStock = 0;
        int outOfStock = 0;

        for (Product product : allProducts) {
            if (product.getStock() <= 0) {
                outOfStock++;
            } else if (product.isLowStock()) {
                lowStock++;
            }
        }

        tvSummary.setText(String.format("Total: %d | Low Stock: %d | Out of Stock: %d", 
                total, lowStock, outOfStock));
    }

    private void filterInventory(String query) {
        List<Product> filtered = new ArrayList<>();

        for (Product product : allProducts) {
            // Check query match
            boolean matchesQuery = query.isEmpty() || 
                product.getName().toLowerCase().contains(query.toLowerCase()) ||
                (product.getBarcode() != null && product.getBarcode().contains(query));

            if (!matchesQuery) continue;

            // Check filter
            boolean matchesFilter = false;
            switch (currentFilter) {
                case "all":
                    matchesFilter = true;
                    break;
                case "low":
                    matchesFilter = product.isLowStock() && product.getStock() > 0;
                    break;
                case "out":
                    matchesFilter = product.getStock() <= 0;
                    break;
            }

            if (matchesFilter) {
                filtered.add(product);
            }
        }

        adapter.updateProducts(filtered);

        if (filtered.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvInventory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvInventory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateStock(Product product, int newStock) {
        product.setStock(newStock);
        database.productDao().update(product);
        loadInventory();
    }
}
