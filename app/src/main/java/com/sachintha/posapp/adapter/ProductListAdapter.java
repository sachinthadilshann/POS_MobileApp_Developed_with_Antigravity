package com.sachintha.posapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.R;
import com.sachintha.posapp.database.entity.Product;
import com.sachintha.posapp.utils.FormatUtils;
import com.sachintha.posapp.utils.SessionManager;

import java.util.List;

/**
 * Adapter for displaying products in a list layout
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductActionListener listener;
    private boolean isAdmin;

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
    }

    public ProductListAdapter(Context context, List<Product> products, OnProductActionListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
        this.isAdmin = SessionManager.getInstance(context).isAdmin();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvBarcode.setText(product.getBarcode() != null ? product.getBarcode() : "No barcode");
        holder.tvPrice.setText(FormatUtils.formatCurrency(product.getPrice()));
        holder.tvStock.setText("Stock: " + product.getStock());

        // Set stock color
        if (product.getStock() <= 0) {
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.error, null));
        } else if (product.isLowStock()) {
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.warning, null));
        } else {
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.success, null));
        }

        // Set inactive state
        if (!product.isActive()) {
            holder.cardView.setAlpha(0.5f);
            holder.tvName.setText(product.getName() + " (Inactive)");
        } else {
            holder.cardView.setAlpha(1f);
        }

        // Show/hide admin actions
        if (isAdmin) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProduct(product);
                }
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
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
        TextView tvName, tvBarcode, tvPrice, tvStock;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvBarcode = itemView.findViewById(R.id.tv_product_barcode);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStock = itemView.findViewById(R.id.tv_product_stock);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
