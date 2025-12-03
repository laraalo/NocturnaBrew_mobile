package com.example.nocturnabrew_mobile;

import android.content.Context;
import android.content.SharedPreferences;

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

}

