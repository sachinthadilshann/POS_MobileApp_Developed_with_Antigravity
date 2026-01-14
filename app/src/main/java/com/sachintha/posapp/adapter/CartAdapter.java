package com.sachintha.posapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.R;
import com.sachintha.posapp.model.Cart;
import com.sachintha.posapp.model.CartItem;
import com.sachintha.posapp.utils.FormatUtils;

import java.util.List;

/**
 * Adapter for displaying cart items
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> items;
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> items, OnCartItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        Cart cart = Cart.getInstance();

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(FormatUtils.formatCurrency(item.getUnitPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotal.setText(FormatUtils.formatCurrency(item.getTotal()));

        holder.btnMinus.setOnClickListener(v -> {
            cart.decrementQuantity(item.getProductId());
            if (listener != null) {
                listener.onQuantityChanged(item);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            cart.incrementQuantity(item.getProductId());
            if (listener != null) {
                listener.onQuantityChanged(item);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageButton btnMinus, btnPlus, btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvTotal = itemView.findViewById(R.id.tv_item_total);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
