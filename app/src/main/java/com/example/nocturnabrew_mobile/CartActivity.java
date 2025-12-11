package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nocturnabrew_mobile.adapters.CartManager;
import com.example.nocturnabrew_mobile.models.OrderRequestItem;
import com.google.gson.Gson;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtSubtotal, txtIVA, txtTotal;
    private Button btnPay, btnBack;
    private ImageButton btnMenu, btnOrders;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        email = prefs.getString("USER_EMAIL", null);

        CartManager.getInstance().loadCart(this, email);

        if (CartManager.getInstance().getItems().isEmpty()) {
            startActivity(new Intent(this, CartEmptyActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerCart);
        txtSubtotal = findViewById(R.id.PriceSubtotal);
        txtIVA = findViewById(R.id.PriceIva);
        txtTotal = findViewById(R.id.PriceTotal);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnback);
        btnMenu = findViewById(R.id.btn_menuSinceCart);
        btnOrders = findViewById(R.id.btn_OrdersSinceCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CartManager.CartAdapter adapter = new CartManager.CartAdapter(
                this,
                CartManager.getInstance().getItems(),
                this::refreshTotals
        );

        recyclerView.setAdapter(adapter);

        refreshTotals();

        btnPay.setOnClickListener(v -> enviarOrdenBackend());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(CartActivity.this, MenuActivity.class);
                startActivity(i2);
            }
        });
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i3 = new Intent(CartActivity.this, MenuActivity.class);
                startActivity(i3);
            }
        });
    }

    private void refreshTotals() {
        txtSubtotal.setText("Subtotal: $" + CartManager.getInstance().getSubtotal());
        txtIVA.setText("IVA: $" + CartManager.getInstance().getIVA());
        txtTotal.setText("Total: $" + CartManager.getInstance().getTotal());

        if (CartManager.getInstance().getItems().isEmpty()) {
            startActivity(new Intent(this, CartEmptyActivity.class));
            finish();
        }

        CartManager.getInstance().saveCart(this, email);
    }

    private void enviarOrdenBackend() {
        Intent i = new Intent(this, QrActivity.class);
        startActivity(i);
    }
}
