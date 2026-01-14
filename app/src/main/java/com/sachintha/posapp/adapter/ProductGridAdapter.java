package com.sachintha.posapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.R;
import com.sachintha.posapp.database.entity.Product;
import com.sachintha.posapp.utils.FormatUtils;

import java.util.List;

/**
 * Adapter for displaying products in a grid layout
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductGridAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(FormatUtils.formatCurrency(product.getPrice()));
        holder.tvStock.setText("Stock: " + product.getStock());

        // Set card appearance based on stock
        if (product.getStock() <= 0) {
            holder.cardView.setAlpha(0.5f);
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.error, null));
        } else if (product.isLowStock()) {
            holder.cardView.setAlpha(0.8f);
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.warning, null));
        } else {
            holder.cardView.setAlpha(1f);
            holder.tvStock.setTextColor(context.getResources().getColor(R.color.text_secondary, null));
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
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
        TextView tvName, tvPrice, tvStock;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvStock = itemView.findViewById(R.id.tv_product_stock);
        }
    }
}
