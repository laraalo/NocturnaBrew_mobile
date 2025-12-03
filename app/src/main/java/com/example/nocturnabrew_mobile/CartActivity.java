package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(CartActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", null);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CartManager.CartAdapter adapter = new CartManager.CartAdapter(
                this,
                CartManager.getInstance().getItems(),
                this::updateTotals
        );

        recyclerView.setAdapter(adapter);

        updateTotals();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(CartActivity.this, android.R.menu.class);
                startActivity(intent2);
            }
        });

        btnPay.setOnClickListener(v -> {
            enviarOrdenBackend();
        });
    }

    private void updateTotals() {
        txtSubtotal.setText("Subtotal: $" + CartManager.getInstance().getSubtotal());
        txtIVA.setText("IVA: $" + CartManager.getInstance().getIVA());
        txtTotal.setText("Total: $" + CartManager.getInstance().getTotal());
    }

    private void enviarOrdenBackend() {
        List<OrderRequestItem> payload = CartManager.getInstance().getOrderItemsForBackend();

        Intent i = new Intent(this, QrActivity.class);
        i.putExtra("order_items", new Gson().toJson(payload));
        i.putExtra("order_total", CartManager.getInstance().getTotal());
        startActivity(i);
    }


}
