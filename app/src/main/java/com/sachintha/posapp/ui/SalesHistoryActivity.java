package com.sachintha.posapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.adapter.SalesAdapter;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Sale;

import java.util.List;

/**
 * Sales History Activity
 * Displays list of all completed sales
 */
public class SalesHistoryActivity extends AppCompatActivity implements SalesAdapter.OnSaleClickListener {

    private ImageButton btnBack;
    private RecyclerView rvSales;
    private TextView tvEmpty;

    private POSDatabase database;
    private SalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        database = POSApplication.getInstance().getDatabase();

        initViews();
        loadSales();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSales();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rvSales = findViewById(R.id.rv_sales);
        tvEmpty = findViewById(R.id.tv_empty);

        btnBack.setOnClickListener(v -> onBackPressed());

        rvSales.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSales() {
        List<Sale> sales = database.saleDao().getAllSales();
        
        if (sales.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSales.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSales.setVisibility(View.VISIBLE);
            
            adapter = new SalesAdapter(this, sales, this);
            rvSales.setAdapter(adapter);
        }
    }

    @Override
    public void onSaleClick(Sale sale) {
        android.content.Intent intent = new android.content.Intent(this, SaleDetailsActivity.class);
        intent.putExtra("sale_id", sale.getId());
        startActivity(intent);
    }
}
