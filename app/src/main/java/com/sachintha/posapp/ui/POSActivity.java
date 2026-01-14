package com.sachintha.posapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.adapter.CartAdapter;
import com.sachintha.posapp.adapter.ProductGridAdapter;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Category;
import com.sachintha.posapp.database.entity.Product;
import com.sachintha.posapp.database.entity.Sale;
import com.sachintha.posapp.database.entity.SaleItem;
import com.sachintha.posapp.model.Cart;
import com.sachintha.posapp.model.CartItem;
import com.sachintha.posapp.utils.FormatUtils;
import com.sachintha.posapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * POS Terminal Activity
 * Main sales interface with product grid, cart, and checkout
 */
public class POSActivity extends AppCompatActivity implements 
        ProductGridAdapter.OnProductClickListener, 
        CartAdapter.OnCartItemActionListener {

    private static final int BARCODE_REQUEST = 1001;

    private EditText etSearch;
    private ImageButton btnScan, btnClearSearch;
    private ChipGroup chipGroupCategories;
    private RecyclerView rvProducts, rvCart;
    private LinearLayout layoutEmptyCart;
    private TextView tvSubtotal, tvDiscount, tvTax, tvTotal, tvItemCount;
    private Button btnDiscount, btnClearCart, btnCheckout;
    private ImageButton btnBack;

    private POSDatabase database;
    private SessionManager sessionManager;
    private Cart cart;

    private ProductGridAdapter productAdapter;
    private CartAdapter cartAdapter;
    private List<Product> allProducts;
    private List<Category> categories;

    private Long selectedCategoryId = null;

    private ActivityResultLauncher<Intent> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        database = POSApplication.getInstance().getDatabase();
        sessionManager = SessionManager.getInstance(this);
        cart = Cart.getInstance();

        initViews();
        setupRecyclerViews();
        setupListeners();
        loadCategories();
        loadProducts();
        updateCartUI();

        // Setup barcode scanner launcher
        barcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra("barcode");
                    if (barcode != null) {
                        handleBarcodeScanned(barcode);
                    }
                }
            }
        );
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnScan = findViewById(R.id.btn_scan);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        chipGroupCategories = findViewById(R.id.chip_group_categories);
        rvProducts = findViewById(R.id.rv_products);
        rvCart = findViewById(R.id.rv_cart);
        layoutEmptyCart = findViewById(R.id.layout_empty_cart);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tvItemCount = findViewById(R.id.tv_item_count);
        btnDiscount = findViewById(R.id.btn_discount);
        btnClearCart = findViewById(R.id.btn_clear_cart);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerViews() {
        // Products grid
        rvProducts.setLayoutManager(new GridLayoutManager(this, 3));
        productAdapter = new ProductGridAdapter(this, new ArrayList<>(), this);
        rvProducts.setAdapter(productAdapter);

        // Cart list
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart.getItems(), this);
        rvCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            barcodeLauncher.launch(intent);
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnDiscount.setOnClickListener(v -> showDiscountDialog());

        btnClearCart.setOnClickListener(v -> {
            if (!cart.isEmpty()) {
                new AlertDialog.Builder(this)
                    .setTitle("Clear Cart")
                    .setMessage("Are you sure you want to clear all items from the cart?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        cart.clear();
                        updateCartUI();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (!cart.isEmpty()) {
                showCheckoutDialog();
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        categories = database.categoryDao().getAllActiveCategories();
        chipGroupCategories.removeAllViews();

        // Add "All" chip
        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategoryId = null;
            filterProducts(etSearch.getText().toString());
        });
        chipGroupCategories.addView(allChip);

        // Add category chips
        for (Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategoryId = category.getId();
                filterProducts(etSearch.getText().toString());
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void loadProducts() {
        allProducts = database.productDao().getAllActiveProducts();
        productAdapter.updateProducts(allProducts);
    }

    private void filterProducts(String query) {
        List<Product> filtered = new ArrayList<>();

        for (Product product : allProducts) {
            boolean matchesQuery = query.isEmpty() || 
                product.getName().toLowerCase().contains(query.toLowerCase()) ||
                (product.getBarcode() != null && product.getBarcode().contains(query));
            
            boolean matchesCategory = selectedCategoryId == null || 
                (product.getCategoryId() != null && product.getCategoryId().equals(selectedCategoryId));

            if (matchesQuery && matchesCategory) {
                filtered.add(product);
            }
        }

        productAdapter.updateProducts(filtered);
    }

    private void handleBarcodeScanned(String barcode) {
        Product product = database.productDao().getProductByBarcode(barcode);
        if (product != null) {
            cart.addProduct(product);
            updateCartUI();
            Toast.makeText(this, "Added: " + product.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product not found: " + barcode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductClick(Product product) {
        if (product.getStock() > 0) {
            cart.addProduct(product);
            updateCartUI();
        } else {
            Toast.makeText(this, "Out of stock!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(CartItem item) {
        updateCartUI();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cart.removeProduct(item.getProductId());
        updateCartUI();
    }

    private void updateCartUI() {
        List<CartItem> items = cart.getItems();
        cartAdapter.updateItems(items);

        if (items.isEmpty()) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
        }

        tvSubtotal.setText(FormatUtils.formatCurrency(cart.getSubtotal()));
        tvDiscount.setText("-" + FormatUtils.formatCurrency(cart.getDiscountAmount()));
        tvTax.setText(FormatUtils.formatCurrency(cart.getTaxAmount()));
        tvTotal.setText(FormatUtils.formatCurrency(cart.getTotal()));
        tvItemCount.setText(cart.getTotalQuantity() + " items");

        btnCheckout.setEnabled(!cart.isEmpty());
        btnClearCart.setEnabled(!cart.isEmpty());
    }

    private void showDiscountDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_discount, null);
        EditText etDiscount = dialogView.findViewById(R.id.et_discount);
        etDiscount.setText(String.valueOf((int) cart.getDiscountPercentage()));

        new AlertDialog.Builder(this)
            .setTitle("Apply Discount")
            .setView(dialogView)
            .setPositiveButton("Apply", (dialog, which) -> {
                try {
                    double discount = Double.parseDouble(etDiscount.getText().toString());
                    if (discount >= 0 && discount <= 100) {
                        cart.setDiscountPercentage(discount);
                        updateCartUI();
                    } else {
                        Toast.makeText(this, "Discount must be between 0 and 100", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid discount value", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showCheckoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_checkout, null);
        TextView tvCheckoutTotal = dialogView.findViewById(R.id.tv_checkout_total);
        EditText etAmountPaid = dialogView.findViewById(R.id.et_amount_paid);
        TextView tvChange = dialogView.findViewById(R.id.tv_change);
        Button btnCash = dialogView.findViewById(R.id.btn_cash);
        Button btnCard = dialogView.findViewById(R.id.btn_card);

        final String[] paymentMethod = {"CASH"};
        double total = cart.getTotal();

        tvCheckoutTotal.setText(FormatUtils.formatCurrency(total));

        btnCash.setOnClickListener(v -> {
            paymentMethod[0] = "CASH";
            btnCash.setAlpha(1f);
            btnCard.setAlpha(0.5f);
        });

        btnCard.setOnClickListener(v -> {
            paymentMethod[0] = "CARD";
            btnCard.setAlpha(1f);
            btnCash.setAlpha(0.5f);
            etAmountPaid.setText(FormatUtils.formatAmount(total));
        });

        etAmountPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double paid = Double.parseDouble(s.toString().replace(",", ""));
                    double change = paid - total;
                    if (change >= 0) {
                        tvChange.setText("Change: " + FormatUtils.formatCurrency(change));
                        tvChange.setTextColor(getResources().getColor(R.color.success, null));
                    } else {
                        tvChange.setText("Remaining: " + FormatUtils.formatCurrency(Math.abs(change)));
                        tvChange.setTextColor(getResources().getColor(R.color.error, null));
                    }
                } catch (NumberFormatException e) {
                    tvChange.setText("Change: " + FormatUtils.formatCurrency(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        new AlertDialog.Builder(this)
            .setTitle("Checkout")
            .setView(dialogView)
            .setPositiveButton("Complete Sale", (dialog, which) -> {
                try {
                    double amountPaid = Double.parseDouble(etAmountPaid.getText().toString().replace(",", ""));
                    if (amountPaid >= total) {
                        completeSale(paymentMethod[0], amountPaid);
                    } else {
                        Toast.makeText(this, "Insufficient payment amount", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void completeSale(String paymentMethod, double amountPaid) {
        // Create sale record
        Sale sale = new Sale();
        sale.setInvoiceNumber(FormatUtils.generateInvoiceNumber());
        sale.setUserId(sessionManager.getUserId());
        sale.setCashierName(sessionManager.getFullName());
        sale.setSaleDate(new Date());
        sale.setSubtotal(cart.getSubtotal());
        sale.setDiscount(cart.getDiscountAmount());
        sale.setTax(cart.getTaxAmount());
        sale.setTotal(cart.getTotal());
        sale.setAmountPaid(amountPaid);
        sale.setChange(amountPaid - cart.getTotal());
        sale.setPaymentMethod(paymentMethod);
        sale.setStatus("COMPLETED");

        long saleId = database.saleDao().insert(sale);

        // Create sale items and update stock
        for (CartItem item : cart.getItems()) {
            SaleItem saleItem = new SaleItem();
            saleItem.setSaleId(saleId);
            saleItem.setProductId(item.getProductId());
            saleItem.setProductName(item.getProductName());
            saleItem.setProductBarcode(item.getBarcode());
            saleItem.setQuantity(item.getQuantity());
            saleItem.setUnitPrice(item.getUnitPrice());
            saleItem.setDiscount(item.getDiscount());
            saleItem.setTotal(item.getTotal());

            database.saleItemDao().insert(saleItem);

            // Decrease stock
            database.productDao().decreaseStock(item.getProductId(), item.getQuantity());
        }

        // Clear cart and refresh products
        cart.clear();
        loadProducts();
        updateCartUI();

        // Show success and offer to print receipt
        new AlertDialog.Builder(this)
            .setTitle("Sale Completed!")
            .setMessage("Invoice: " + sale.getInvoiceNumber() + "\n" +
                    "Total: " + FormatUtils.formatCurrency(sale.getTotal()) + "\n" +
                    "Change: " + FormatUtils.formatCurrency(sale.getChange()))
            .setPositiveButton("Print Receipt", (dialog, which) -> {
                // Open sale details for printing
                Intent intent = new Intent(this, SaleDetailsActivity.class);
                intent.putExtra("sale_id", saleId);
                startActivity(intent);
            })
            .setNegativeButton("Done", null)
            .show();
    }

    @Override
    public void onBackPressed() {
        if (!cart.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("Abandon Cart?")
                .setMessage("You have items in your cart. Are you sure you want to go back?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cart.clear();
                    super.onBackPressed();
                })
                .setNegativeButton("No", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
}
