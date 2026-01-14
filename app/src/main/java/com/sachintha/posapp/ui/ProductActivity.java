package com.sachintha.posapp.ui;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.adapter.ProductListAdapter;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Product;
import com.sachintha.posapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Product Management Activity
 * List, search, add, edit, and delete products
 */
public class ProductActivity extends AppCompatActivity implements ProductListAdapter.OnProductActionListener {

    private ImageButton btnBack;
    private EditText etSearch;
    private RecyclerView rvProducts;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;

    private POSDatabase database;
    private SessionManager sessionManager;
    private ProductListAdapter adapter;
    private List<Product> allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        database = POSApplication.getInstance().getDatabase();
        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etSearch = findViewById(R.id.et_search);
        rvProducts = findViewById(R.id.rv_products);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductListAdapter(this, new ArrayList<>(), this);
        rvProducts.setAdapter(adapter);

        // Only admins can add products
        if (!sessionManager.isAdmin()) {
            fabAdd.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        allProducts = database.productDao().getAllProducts();
        adapter.updateProducts(allProducts);
        updateEmptyState();
    }

    private void filterProducts(String query) {
        if (allProducts == null) return;

        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                (product.getBarcode() != null && product.getBarcode().contains(query))) {
                filtered.add(product);
            }
        }
        adapter.updateProducts(filtered);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
            .setPositiveButton("Delete", (dialog, which) -> {
                product.setActive(false);
                database.productDao().update(product);
                loadProducts();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
