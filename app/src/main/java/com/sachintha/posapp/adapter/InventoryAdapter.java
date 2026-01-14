package com.sachintha.posapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.R;
import com.sachintha.posapp.database.entity.Product;

import java.util.List;

/**
 * Adapter for inventory list with stock update functionality
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private OnInventoryActionListener listener;

    public interface OnInventoryActionListener {
        void onUpdateStock(Product product, int newStock);
    }

    public InventoryAdapter(Context context, List<Product> products, OnInventoryActionListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvBarcode.setText(product.getBarcode() != null ? product.getBarcode() : "No barcode");
        holder.tvStock.setText(String.valueOf(product.getStock()));
        holder.tvMinStock.setText("Min: " + product.getMinStock());

        // Set stock status
        if (product.getStock() <= 0) {
            holder.tvStatus.setText("OUT OF STOCK");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.error, null));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.error_bg, null));
        } else if (product.isLowStock()) {
            holder.tvStatus.setText("LOW STOCK");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.warning, null));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.warning_bg, null));
        } else {
            holder.tvStatus.setText("IN STOCK");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.success, null));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.card_background, null));
        }

        holder.btnUpdateStock.setOnClickListener(v -> showUpdateStockDialog(product));
    }

    private void showUpdateStockDialog(Product product) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_stock, null);
        EditText etStock = dialogView.findViewById(R.id.et_stock);
        etStock.setText(String.valueOf(product.getStock()));

        new AlertDialog.Builder(context)
            .setTitle("Update Stock: " + product.getName())
            .setView(dialogView)
            .setPositiveButton("Update", (dialog, which) -> {
                try {
                    int newStock = Integer.parseInt(etStock.getText().toString());
                    if (newStock >= 0 && listener != null) {
                        listener.onUpdateStock(product, newStock);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvBarcode, tvStock, tvMinStock, tvStatus;
        ImageButton btnUpdateStock;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_inventory);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvBarcode = itemView.findViewById(R.id.tv_product_barcode);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvMinStock = itemView.findViewById(R.id.tv_min_stock);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnUpdateStock = itemView.findViewById(R.id.btn_update_stock);
        }
    }
}
