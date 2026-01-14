package com.sachintha.posapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sachintha.posapp.R;
import com.sachintha.posapp.database.entity.SaleItem;
import com.sachintha.posapp.utils.FormatUtils;

import java.util.List;

/**
 * Adapter for displaying sale items in receipt
 */
public class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder> {

    private List<SaleItem> items;

    public SaleItemsAdapter(List<SaleItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleItem item = items.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvPrice.setText(FormatUtils.formatCurrency(item.getUnitPrice()));
        holder.tvTotal.setText(FormatUtils.formatCurrency(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice, tvTotal;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvTotal = itemView.findViewById(R.id.tv_item_total);
        }
    }
}
