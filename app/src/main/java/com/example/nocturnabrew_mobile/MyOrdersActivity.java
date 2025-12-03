package com.example.nocturnabrew_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nocturnabrew_mobile.adapters.OrdersAdapter;
import com.example.nocturnabrew_mobile.models.OrderResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<OrderResponse.Order> ordersList;
    private String userEmail;
    private ImageButton btnMenu, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        userEmail = getIntent().getStringExtra("email");

        recyclerView = findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();

        adapter = new OrdersAdapter(ordersList, orderId -> cancelOrder(orderId));
        recyclerView.setAdapter(adapter);
        btnMenu = findViewById(R.id.btn_cart);
        btnMenu = findViewById(R.id.btn_home);
    }

    private void loadOrders() {
        SharedPreferences prefs = getSharedPreferences("ORDERS_DB", MODE_PRIVATE);
        String json = prefs.getString("orders_" + userEmail, "[]");

        Type type = new TypeToken<List<OrderResponse.Order>>() {}.getType();
        ordersList = new Gson().fromJson(json, type);
    }

    private void cancelOrder(String orderId) {
        // Aquí llamas tu endpoint updateStatus
        Toast.makeText(this, "Cancelar orden: " + orderId, Toast.LENGTH_SHORT).show();
    }
}
