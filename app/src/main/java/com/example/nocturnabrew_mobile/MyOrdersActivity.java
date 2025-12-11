package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nocturnabrew_mobile.adapters.OrdersAdapter;
import com.example.nocturnabrew_mobile.api.ApiService;
import com.example.nocturnabrew_mobile.models.GenericResponse;
import com.example.nocturnabrew_mobile.models.OrderResponse;
import com.example.nocturnabrew_mobile.models.UpdateStatusRequest;
import com.example.nocturnabrew_mobile.network.RetrofitInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private OrdersAdapter adapter;
    private List<OrderResponse.Order> orderList = new ArrayList<>();
    private String userEmail;
    private String userToken;
    private ImageButton btnMenu, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userEmail = prefs.getString("USER_EMAIL", "");
        userToken = prefs.getString("USER_TOKEN", "");

        loadOrders();

        adapter = new OrdersAdapter(orderList, this::cancelOrder);
        recyclerOrders.setAdapter(adapter);
        btnCart = findViewById(R.id.btn_cartSinceMyOrders);
        btnMenu = findViewById(R.id.btn_menuSinceMyOrders);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyOrdersActivity.this, CartActivity.class);
                startActivity(i);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(MyOrdersActivity.this, MenuActivity.class);
                startActivity(i2);
            }
        });
    }

    private void loadOrders() {
        SharedPreferences prefs = getSharedPreferences("ORDERS_DB", MODE_PRIVATE);
        String key = "orders_" + userEmail;

        String json = prefs.getString(key, "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<OrderResponse.Order>>() {}.getType();

        orderList.clear();
        orderList.addAll(gson.fromJson(json, type));
    }
    // EN MyOrdersActivity.java

    // Agrega este método
    private void saveOrders() {
        SharedPreferences prefs = getSharedPreferences("ORDERS_DB", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = "orders_" + userEmail;

        Gson gson = new Gson();
        String json = gson.toJson(orderList); // Guarda la lista completa y actualizada

        editor.putString(key, json);
        editor.apply();
    }

    private void cancelOrder(String orderId, Button btnCancel, TextView txtStatus) {
        ApiService api = RetrofitInstance.getApiService();
        String header = "UserToken " + userToken;

        Call<GenericResponse> call = api.updateOrderStatus(header, orderId, new UpdateStatusRequest("canceled"));


        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MyOrdersActivity.this,
                            "Error al cancelar orden", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(MyOrdersActivity.this, "Orden cancelada", Toast.LENGTH_SHORT).show();

                // 1. ACTUALIZAR EL ESTADO LOCALMENTE Y GUARDAR
                for (OrderResponse.Order order : orderList) {
                    if (order.getOrderId().equals(orderId)) {
                        order.setStatus("canceled"); // <-- ¡Aquí se aplica el cambio!
                        break;
                    }
                }
                saveOrders(); // <-- Guarda la lista actualizada en SharedPreferences.

                // 2. Notificar al Adapter para que se actualice la UI
                loadOrders();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(MyOrdersActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

