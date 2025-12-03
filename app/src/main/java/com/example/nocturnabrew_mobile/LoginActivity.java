package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.nocturnabrew_mobile.models.*;
import com.example.nocturnabrew_mobile.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnEnter, btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.EmailLogin);
        password = findViewById(R.id.PasswordLogin);
        btnEnter = findViewById(R.id.buttonEnter);
        btnBack = findViewById(R.id.buttonBack);
        btnEnter.setOnClickListener(v -> LoginAction());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void LoginAction(){
        LoginRequest userToLogin = new LoginRequest(email.getText().toString(), password.getText().toString());
        Call<LoginResponse> call = RetrofitInstance.getApiService().loginUser(userToLogin);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = response.body().getUser();
                String token = response.body().getToken();
                SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                prefs.edit()
                        .putString("USER_TOKEN", token)
                        .putString("USER_EMAIL", user.getEmail())  // ← GUARDAR EMAIL
                        .apply();// guardar token y email
                Toast.makeText(LoginActivity.this, "Welcome: " + user.getName(), Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(LoginActivity.this, MenuActivity.class);                intent2.putExtra("userName", user.getName());
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}