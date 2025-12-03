package com.example.nocturnabrew_mobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nocturnabrew_mobile.R;
import com.example.nocturnabrew_mobile.models.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Añadir productos al carrito
    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.increaseQuantity();
                return;
            }
        }

        cartItems.add(new CartItem(product, 1));
    }

    // Remover item completo
    public void removeProduct(int productId) {
        cartItems.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    // Vaciar carrito
    public void clearCart() {
        cartItems.clear();
    }

    // Obtener lista
    public List<CartItem> getItems() {
        return cartItems;
    }

    // Subtotal local
    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getProduct().getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    // IVA local (16%)
    public double getIVA() {
        return getSubtotal() * 0.16;
    }

    // Total local
    public double getTotal() {
        return getSubtotal() + getIVA();
    }

    // Para enviar al backend
    public List<OrderRequestItem> getOrderItemsForBackend() {
        List<OrderRequestItem> list = new ArrayList<>();
        for (CartItem item : cartItems) {
            list.add(new OrderRequestItem(
                    item.getProduct().getProductId(), // el ID que tu backend usa
                    item.getQuantity()
            ));
        }
        return list;
    }
    public void saveCart(Context context, String userEmail) {
        SharedPreferences prefs = context.getSharedPreferences("CART_PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        prefs.edit().putString("CART_USER_" + userEmail, json).apply();
    }

    public void loadCart(Context context, String userEmail) {
        SharedPreferences prefs = context.getSharedPreferences("CART_PREFS", Context.MODE_PRIVATE);
        String json = prefs.getString("CART_USER_" + userEmail, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CartItem>>() {}.getType();
            List<CartItem> savedList = gson.fromJson(json, type);

            cartItems.clear();
            cartItems.addAll(savedList);
        }
    }

    public static class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        private Context context;
        private List<CartItem> cartItems;
        private Runnable onTotalsChanged;

        public CartAdapter(Context context, List<CartItem> cartItems, Runnable onTotalsChanged) {
            this.context = context;
            this.cartItems = cartItems;
            this.onTotalsChanged = onTotalsChanged;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItem item = cartItems.get(position);

            holder.name.setText(item.getProduct().getName());
            holder.price.setText("$" + item.getProduct().getPrice());
            holder.qty.setText(String.valueOf(item.getQuantity()));

            Glide.with(context)
                    .load(item.getProduct().getUrl())
                    .into(holder.image);

            holder.btnIncrease.setOnClickListener(v -> {
                item.increaseQuantity();
                notifyItemChanged(position);
                onTotalsChanged.run();
            });

            holder.btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.decreaseQuantity();
                } else {
                    getInstance().removeProduct(item.getProduct().getProductId());
                    notifyItemRemoved(position);
                }
                notifyDataSetChanged();
                onTotalsChanged.run();
            });

            holder.btnRemove.setOnClickListener(v -> {
                getInstance().removeProduct(item.getProduct().getProductId());
                notifyItemRemoved(position);
                notifyDataSetChanged();
                onTotalsChanged.run();
            });
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView name, price, qty;
            ImageButton btnIncrease, btnDecrease, btnRemove;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.cartProductImage);
                name = itemView.findViewById(R.id.cartProductName);
                price = itemView.findViewById(R.id.cartProductPrice);
                qty = itemView.findViewById(R.id.cartProductQty);
                btnIncrease = itemView.findViewById(R.id.btnIncrease);
                btnDecrease = itemView.findViewById(R.id.btnDecrease);
                btnRemove = itemView.findViewById(R.id.btnRemove);
            }
        }
    }
}

