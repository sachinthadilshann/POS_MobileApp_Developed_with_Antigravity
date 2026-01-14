package com.sachintha.posapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Category;
import com.sachintha.posapp.database.entity.Product;

import java.util.List;

/**
 * Add/Edit Product Activity
 */
public class AddEditProductActivity extends AppCompatActivity {

    private ImageButton btnBack, btnScanBarcode;
    private TextView tvTitle;
    private EditText etName, etBarcode, etDescription, etPrice, etCostPrice, etStock, etMinStock;
    private Spinner spinnerCategory;
    private Button btnSave, btnCancel;

    private POSDatabase database;
    private Product product;
    private boolean isEditMode = false;
    private List<Category> categories;

    private ActivityResultLauncher<Intent> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        database = POSApplication.getInstance().getDatabase();

        initViews();
        setupListeners();
        loadCategories();
        checkEditMode();

        // Setup barcode scanner launcher
        barcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra("barcode");
                    if (barcode != null) {
                        etBarcode.setText(barcode);
                    }
                }
            }
        );
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnScanBarcode = findViewById(R.id.btn_scan_barcode);
        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_name);
        etBarcode = findViewById(R.id.et_barcode);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etCostPrice = findViewById(R.id.et_cost_price);
        etStock = findViewById(R.id.et_stock);
        etMinStock = findViewById(R.id.et_min_stock);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnCancel.setOnClickListener(v -> onBackPressed());
        btnSave.setOnClickListener(v -> saveProduct());
        btnScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeLauncher.launch(intent);
        });
    }

    private void loadCategories() {
        categories = database.categoryDao().getAllActiveCategories();
        
        // Add "No Category" option
        Category noCategory = new Category();
        noCategory.setId(0);
        noCategory.setName("-- Select Category --");
        categories.add(0, noCategory);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void checkEditMode() {
        long productId = getIntent().getLongExtra("product_id", -1);
        if (productId != -1) {
            isEditMode = true;
            product = database.productDao().getProductById(productId);
            if (product != null) {
                populateFields();
                tvTitle.setText("Edit Product");
            }
        } else {
            tvTitle.setText("Add Product");
            product = new Product();
        }
    }

    private void populateFields() {
        etName.setText(product.getName());
        etBarcode.setText(product.getBarcode());
        etDescription.setText(product.getDescription());
        etPrice.setText(String.valueOf(product.getPrice()));
        etCostPrice.setText(String.valueOf(product.getCostPrice()));
        etStock.setText(String.valueOf(product.getStock()));
        etMinStock.setText(String.valueOf(product.getMinStock()));

        // Select category
        if (product.getCategoryId() != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == product.getCategoryId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveProduct() {
        // Validate input
        String name = etName.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String costPriceStr = etCostPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String minStockStr = etMinStock.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Product name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        double price, costPrice = 0;
        int stock = 0, minStock = 10;

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price");
            return;
        }

        if (!TextUtils.isEmpty(costPriceStr)) {
            try {
                costPrice = Double.parseDouble(costPriceStr);
            } catch (NumberFormatException e) {
                etCostPrice.setError("Invalid cost price");
                return;
            }
        }

        if (!TextUtils.isEmpty(stockStr)) {
            try {
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                etStock.setError("Invalid stock");
                return;
            }
        }

        if (!TextUtils.isEmpty(minStockStr)) {
            try {
                minStock = Integer.parseInt(minStockStr);
            } catch (NumberFormatException e) {
                etMinStock.setError("Invalid minimum stock");
                return;
            }
        }

        // Check barcode uniqueness
        if (!TextUtils.isEmpty(barcode)) {
            Product existing = database.productDao().getProductByBarcode(barcode);
            if (existing != null && existing.getId() != product.getId()) {
                etBarcode.setError("Barcode already exists");
                etBarcode.requestFocus();
                return;
            }
        }

        // Set product fields
        product.setName(name);
        product.setBarcode(TextUtils.isEmpty(barcode) ? null : barcode);
        product.setDescription(TextUtils.isEmpty(description) ? null : description);
        product.setPrice(price);
        product.setCostPrice(costPrice);
        product.setStock(stock);
        product.setMinStock(minStock);
        product.setActive(true);

        // Set category
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        if (selectedCategory != null && selectedCategory.getId() != 0) {
            product.setCategoryId(selectedCategory.getId());
        } else {
            product.setCategoryId(null);
        }

        // Save to database
        if (isEditMode) {
            database.productDao().update(product);
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            database.productDao().insert(product);
            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
