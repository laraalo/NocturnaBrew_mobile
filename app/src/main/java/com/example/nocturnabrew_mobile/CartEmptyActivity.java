package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CartEmptyActivity extends AppCompatActivity {
    private Button btnGoShopping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_empty);
        btnGoShopping = findViewById(R.id.btnGoShopping);
        btnGoShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartEmptyActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}