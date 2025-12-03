package com.example.nocturnabrew_mobile.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nocturnabrew_mobile.R;
import com.example.nocturnabrew_mobile.models.OrderResponse;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<OrderResponse.Order> orders;
    private OnCancelClickListener cancelListener;

    public interface OnCancelClickListener {
        void onCancel(String orderId);
    }

    public OrdersAdapter(List<OrderResponse.Order> orders, OnCancelClickListener listener) {
        this.orders = orders;
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse.Order order = orders.get(position);

        holder.txtOrderId.setText("Order ID: " + order.getOrderId());
        holder.txtOrderTotal.setText("Total: $" + order.getTotal());

        holder.itemsContainer.removeAllViews();

        for (OrderResponse.Order.Item item : order.getItems()) {
            TextView tv = new TextView(holder.itemView.getContext());
            tv.setText("- " + item.getName() + " x" + item.getQty() + " ($" + item.getSubtotal() + ")");
            tv.setTextColor(Color.parseColor("#4E342E"));
            holder.itemsContainer.addView(tv);
        }

        holder.btnCancel.setOnClickListener(v -> {
            cancelListener.onCancel(order.getOrderId());
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderTotal;
        LinearLayout itemsContainer;
        Button btnCancel;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderTotal = itemView.findViewById(R.id.txt_order_total);
            itemsContainer = itemView.findViewById(R.id.items_container);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}

