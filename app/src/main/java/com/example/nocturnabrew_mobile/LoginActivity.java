package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.os.Bundle;
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
        Call<GenericResponse> call = RetrofitInstance.getApiService().loginUser(userToLogin);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                //GenericResponse<User> userLoged = response.body().as;
                Toast.makeText(LoginActivity.this, "Welcome: ", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent2);
                finish();
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Error: ", Toast.LENGTH_LONG).show();

            }
        });
    }
}