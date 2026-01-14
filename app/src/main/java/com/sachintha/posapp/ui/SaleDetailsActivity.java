package com.sachintha.posapp.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.POSApplication;
import com.sachintha.posapp.R;
import com.sachintha.posapp.adapter.SaleItemsAdapter;
import com.sachintha.posapp.database.POSDatabase;
import com.sachintha.posapp.database.entity.Sale;
import com.sachintha.posapp.database.entity.SaleItem;
import com.sachintha.posapp.utils.FormatUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Sale Details Activity
 * Shows complete sale information and receipt
 */
public class SaleDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvInvoice, tvDate, tvCashier, tvPaymentMethod;
    private TextView tvSubtotal, tvDiscount, tvTax, tvTotal;
    private TextView tvAmountPaid, tvChange, tvStatus;
    private RecyclerView rvItems;
    private Button btnPrint;
    private LinearLayout receiptLayout;

    private POSDatabase database;
    private Sale sale;
    private List<SaleItem> saleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        database = POSApplication.getInstance().getDatabase();

        initViews();
        loadSaleDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvInvoice = findViewById(R.id.tv_invoice);
        tvDate = findViewById(R.id.tv_date);
        tvCashier = findViewById(R.id.tv_cashier);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tvAmountPaid = findViewById(R.id.tv_amount_paid);
        tvChange = findViewById(R.id.tv_change);
        tvStatus = findViewById(R.id.tv_status);
        rvItems = findViewById(R.id.rv_items);
        btnPrint = findViewById(R.id.btn_print);
        receiptLayout = findViewById(R.id.receipt_layout);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnPrint.setOnClickListener(v -> printReceipt());

        rvItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSaleDetails() {
        long saleId = getIntent().getLongExtra("sale_id", -1);
        if (saleId == -1) {
            Toast.makeText(this, "Sale not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sale = database.saleDao().getSaleById(saleId);
        if (sale == null) {
            Toast.makeText(this, "Sale not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saleItems = database.saleItemDao().getSaleItemsBySaleId(saleId);

        // Populate views
        tvInvoice.setText(sale.getInvoiceNumber());
        tvDate.setText(FormatUtils.formatDateTime(sale.getSaleDate()));
        tvCashier.setText(sale.getCashierName());
        tvPaymentMethod.setText(sale.getPaymentMethod());
        tvSubtotal.setText(FormatUtils.formatCurrency(sale.getSubtotal()));
        tvDiscount.setText("-" + FormatUtils.formatCurrency(sale.getDiscount()));
        tvTax.setText(FormatUtils.formatCurrency(sale.getTax()));
        tvTotal.setText(FormatUtils.formatCurrency(sale.getTotal()));
        tvAmountPaid.setText(FormatUtils.formatCurrency(sale.getAmountPaid()));
        tvChange.setText(FormatUtils.formatCurrency(sale.getChange()));
        tvStatus.setText(sale.getStatus());

        // Set status color
        if ("COMPLETED".equals(sale.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(R.color.success, null));
        } else if ("REFUNDED".equals(sale.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(R.color.warning, null));
        } else {
            tvStatus.setTextColor(getResources().getColor(R.color.error, null));
        }

        // Setup items list
        SaleItemsAdapter adapter = new SaleItemsAdapter(saleItems);
        rvItems.setAdapter(adapter);
    }

    private void printReceipt() {
        // Create PDF document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        int y = 30;
        int lineHeight = 18;

        // Header
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("SUPERMARKET POS", 150, y, paint);
        y += lineHeight + 5;
        
        paint.setTextSize(10);
        canvas.drawText("Your Trusted Shopping Partner", 150, y, paint);
        y += lineHeight + 10;

        // Invoice info
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(11);
        canvas.drawText("Invoice: " + sale.getInvoiceNumber(), 20, y, paint);
        y += lineHeight;
        canvas.drawText("Date: " + FormatUtils.formatDateTime(sale.getSaleDate()), 20, y, paint);
        y += lineHeight;
        canvas.drawText("Cashier: " + sale.getCashierName(), 20, y, paint);
        y += lineHeight + 5;

        // Separator
        canvas.drawLine(20, y, 280, y, paint);
        y += lineHeight;

        // Items header
        paint.setTextSize(10);
        canvas.drawText("Item", 20, y, paint);
        canvas.drawText("Qty", 160, y, paint);
        canvas.drawText("Price", 200, y, paint);
        canvas.drawText("Total", 250, y, paint);
        y += lineHeight;

        // Items
        for (SaleItem item : saleItems) {
            String name = item.getProductName();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }
            canvas.drawText(name, 20, y, paint);
            canvas.drawText(String.valueOf(item.getQuantity()), 160, y, paint);
            canvas.drawText(FormatUtils.formatAmount(item.getUnitPrice()), 200, y, paint);
            canvas.drawText(FormatUtils.formatAmount(item.getTotal()), 250, y, paint);
            y += lineHeight;
        }

        // Separator
        y += 5;
        canvas.drawLine(20, y, 280, y, paint);
        y += lineHeight;

        // Totals
        paint.setTextSize(11);
        canvas.drawText("Subtotal:", 150, y, paint);
        canvas.drawText(FormatUtils.formatAmount(sale.getSubtotal()), 230, y, paint);
        y += lineHeight;

        if (sale.getDiscount() > 0) {
            canvas.drawText("Discount:", 150, y, paint);
            canvas.drawText("-" + FormatUtils.formatAmount(sale.getDiscount()), 230, y, paint);
            y += lineHeight;
        }

        if (sale.getTax() > 0) {
            canvas.drawText("Tax:", 150, y, paint);
            canvas.drawText(FormatUtils.formatAmount(sale.getTax()), 230, y, paint);
            y += lineHeight;
        }

        paint.setTextSize(14);
        canvas.drawText("TOTAL:", 150, y, paint);
        canvas.drawText(FormatUtils.formatCurrency(sale.getTotal()), 220, y, paint);
        y += lineHeight + 5;

        paint.setTextSize(11);
        canvas.drawText("Paid (" + sale.getPaymentMethod() + "):", 150, y, paint);
        canvas.drawText(FormatUtils.formatAmount(sale.getAmountPaid()), 230, y, paint);
        y += lineHeight;

        canvas.drawText("Change:", 150, y, paint);
        canvas.drawText(FormatUtils.formatAmount(sale.getChange()), 230, y, paint);
        y += lineHeight + 10;

        // Footer
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(10);
        canvas.drawText("Thank you for shopping with us!", 150, y, paint);
        y += lineHeight;
        canvas.drawText("Please come again!", 150, y, paint);

        document.finishPage(page);

        // Save PDF
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "Receipt_" + sale.getInvoiceNumber() + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            fos.close();
            document.close();

            Toast.makeText(this, "Receipt saved to Downloads folder", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
