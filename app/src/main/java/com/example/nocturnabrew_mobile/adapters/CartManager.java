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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    // Añadir productos
    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.increaseQuantity();
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
    }

    // Remover por productId (entero)
    public void removeProduct(int productId) {
        cartItems.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    // Limpiar carrito completo
    public void clearAndSaveCart(Context context, String userEmail) {
        cartItems.clear();
        saveCart(context, userEmail);
    }

    public List<CartItem> getItems() {
        return cartItems;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getProduct().getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    public double getIVA() {
        // 1. Calcular el IVA
        double ivaCalculado = getSubtotal() * 0.16;

        // 2. Redondear el IVA a dos decimales
        BigDecimal bd = new BigDecimal(ivaCalculado);
        // Usamos setScale para redondear a 2 decimales (ej. 34.08)
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue(); // Devuelve 34.08 (preciso)
    }

    public double getTotal() {
        // 1. Convertir los valores a BigDecimal para sumar sin error de precisión
        BigDecimal subtotalBD = new BigDecimal(getSubtotal());
        BigDecimal ivaBD = new BigDecimal(getIVA()); // Usa el IVA ya redondeado

        // 2. Realizar la suma (BigDecimal.add())
        BigDecimal totalBD = subtotalBD.add(ivaBD);

        // 3. Redondear el resultado final a 2 decimales para asegurar la precisión final
        totalBD = totalBD.setScale(2, RoundingMode.HALF_UP);

        // 4. Devolver el resultado
        return totalBD.doubleValue();
    }
    // Para backend
    public List<OrderRequestItem> getOrderItemsForBackend() {
        List<OrderRequestItem> list = new ArrayList<>();
        for (CartItem item : cartItems) {
            list.add(new OrderRequestItem(
                    item.getProduct().getProductId(),
                    item.getQuantity()
            ));
        }
        return list;
    }

    // Guardar carrito en SharedPreferences
    public void saveCart(Context context, String userEmail) {
        SharedPreferences prefs = context.getSharedPreferences("CART_PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        prefs.edit().putString("CART_USER_" + userEmail, json).apply();
    }

    // Cargar carrito
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

    // ------------------ ADAPTER ------------------ //

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

            // + Cantidad
            holder.btnIncrease.setOnClickListener(v -> {
                item.increaseQuantity();
                notifyItemChanged(position);
                onTotalsChanged.run();
            });

            // - Cantidad
            holder.btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.decreaseQuantity();
                    notifyItemChanged(position);
                } else {
                    CartManager.getInstance().removeProduct(item.getProduct().getProductId());
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItems.size());
                }
                onTotalsChanged.run();
            });

            //  Eliminar item
            holder.btnRemove.setOnClickListener(v -> {
                CartManager.getInstance().removeProduct(item.getProduct().getProductId());
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
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



