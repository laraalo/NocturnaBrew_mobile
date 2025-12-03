package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nocturnabrew_mobile.api.ApiService;
import com.example.nocturnabrew_mobile.models.Product;
import com.example.nocturnabrew_mobile.models.ProductResponse;
import com.example.nocturnabrew_mobile.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private ProductAdapter adapter;
    private TextView greetingText;

    private ImageButton btnCart, btnIced, btnHot, btnSweet, btnSavory;
    private long backPressedTime = 0;

    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (backPressedTime + 2000 > System.currentTimeMillis()) {

                    new AlertDialog.Builder(MenuActivity.this)
                            .setTitle("¿Salir?")
                            .setMessage("¿Seguro que quieres cerrar la aplicación?")
                            .setPositiveButton("Sí", (dialog, which) -> finishAffinity())
                            .setNegativeButton("Cancelar", null)
                            .show();

                } else {
                    Toast.makeText(MenuActivity.this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
                }

                backPressedTime = System.currentTimeMillis();
            }
        });
        greetingText = findViewById(R.id.greetingRL);

        Intent intent = getIntent();
        String nameGreeting = intent.getStringExtra("userName");
        greetingText.setText("Good morning, " + nameGreeting);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProductAdapter(MenuActivity.this, new ArrayList<>());
        recyclerProducts.setAdapter(adapter);

        // Obtener UserToken
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        userToken = prefs.getString("USER_TOKEN", null);

        // Referencias a botones de categoría
        btnIced = findViewById(R.id.btn_iced);
        btnHot = findViewById(R.id.btn_hot);
        btnSweet = findViewById(R.id.btn_sweet);
        btnSavory = findViewById(R.id.btn_savory);
        btnCart = findViewById(R.id.btn_cart);


        // Listeners
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MenuActivity.this, CartActivity.class);
                startActivity(intent2);
            }
        });
        btnIced.setOnClickListener(v -> loadProductsByCategory("Iced Favorites"));
        btnHot.setOnClickListener(v -> loadProductsByCategory("Hot Favorites"));
        btnSweet.setOnClickListener(v -> loadProductsByCategory("Sweet Delicacies"));
        btnSavory.setOnClickListener(v -> loadProductsByCategory("Savory Delicacies"));

        // Cargar productos al inicio
        loadProducts();
    }

    private void loadProducts() {

        String headerUser = "UserToken " + userToken;
        Call<ProductResponse> call = RetrofitInstance.getApiService().getProducts(headerUser);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    adapter.updateList(response.body().getValues());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadProductsByCategory(String category) {

        String headerUser = "UserToken " + userToken;
        Call<ProductResponse> call = RetrofitInstance.getApiService().getProductsByCategory(category, headerUser);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Product> products = response.body().getValues();

                    adapter.updateList(products);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
