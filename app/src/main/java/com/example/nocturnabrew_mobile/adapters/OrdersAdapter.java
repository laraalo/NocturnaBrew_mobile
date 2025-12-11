package com.example.nocturnabrew_mobile.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.Collections;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrderResponse.Order> orders;
    private OnOrderCancelListener cancelListener;

    public interface OnOrderCancelListener {
        void onCancel(String orderId, Button btnCancel, TextView txtStatus);
    }

    public OrdersAdapter(List<OrderResponse.Order> orders, OnOrderCancelListener listener) {
        Collections.reverse(orders); //
        this.orders = orders;
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    // EN OrdersAdapter.java, dentro de onBindViewHolder

    // EN OrdersAdapter.java, dentro de onBindViewHolder

    // EN OrdersAdapter.java, dentro de onBindViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse.Order order = orders.get(position);
        // Asegurarse que el estado por defecto sea "pending" si es null
        String status = order.getStatus() != null ? order.getStatus() : "pending";

        // --- CONFIGURACIÓN DE DATOS (SIEMPRE NECESARIA) ---
        holder.orderId.setText("Order: " + order.getOrderId());
        holder.total.setText("Total: $" + order.getTotal());

        StringBuilder sb = new StringBuilder();
        for (OrderResponse.Order.Item item : order.getItems()) {
            sb.append(item.getName())
                    .append(" x")
                    .append(item.getQty())
                    .append("\n");
        }
        holder.items.setText(sb.toString());

        // Configura el texto del estado
        holder.txtStatus.setText(status.toUpperCase());
        // --------------------------------------------------

        if (status.equals("canceled")) {
            // --- ESTADO: CANCELADO ---

            // 1. Ocultar el botón y resaltar el texto
            holder.btnCancel.setVisibility(View.GONE); // Desaparecer el botón

            // 2. Estilos para el estado "CANCELADO"
            holder.txtStatus.setVisibility(View.VISIBLE);
            holder.txtStatus.setTextColor(Color.RED);
            holder.txtStatus.setTypeface(null, Typeface.BOLD);

            // 3. Eliminar el Listener
            holder.btnCancel.setOnClickListener(null);

        } else {
            // --- ESTADO: ACTIVO / PENDIENTE ---

            // 1. Mostrar el botón
            holder.btnCancel.setVisibility(View.VISIBLE);

            // 2. Estilos por defecto
            holder.txtStatus.setTextColor(Color.BLACK); // Usar el color normal
            holder.txtStatus.setTypeface(null, Typeface.NORMAL);

            // 3. Configurar el Listener para permitir la cancelación
            holder.btnCancel.setOnClickListener(v -> {
                cancelListener.onCancel(order.getOrderId(),
                        holder.btnCancel,
                        holder.txtStatus);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, total, items, txtStatus;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.txtOrderId);
            total = itemView.findViewById(R.id.txtOrderTotal);
            items = itemView.findViewById(R.id.txtOrderItems);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}

