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
import com.sachintha.posapp.database.entity.Sale;
import com.sachintha.posapp.utils.FormatUtils;

import java.util.List;

/**
 * Adapter for displaying sales history
 */
public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    private Context context;
    private List<Sale> sales;
    private OnSaleClickListener listener;

    public interface OnSaleClickListener {
        void onSaleClick(Sale sale);
    }

    public SalesAdapter(Context context, List<Sale> sales, OnSaleClickListener listener) {
        this.context = context;
        this.sales = sales;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sale sale = sales.get(position);

        holder.tvInvoice.setText(sale.getInvoiceNumber());
        holder.tvDate.setText(FormatUtils.formatDateTime(sale.getSaleDate()));
        holder.tvCashier.setText(sale.getCashierName());
        holder.tvTotal.setText(FormatUtils.formatCurrency(sale.getTotal()));
        holder.tvPayment.setText(sale.getPaymentMethod());
        holder.tvStatus.setText(sale.getStatus());

        // Set status color
        if ("COMPLETED".equals(sale.getStatus())) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.success, null));
        } else if ("REFUNDED".equals(sale.getStatus())) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.warning, null));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.error, null));
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaleClick(sale);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvInvoice, tvDate, tvCashier, tvTotal, tvPayment, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_sale);
            tvInvoice = itemView.findViewById(R.id.tv_invoice);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCashier = itemView.findViewById(R.id.tv_cashier);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvPayment = itemView.findViewById(R.id.tv_payment);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
