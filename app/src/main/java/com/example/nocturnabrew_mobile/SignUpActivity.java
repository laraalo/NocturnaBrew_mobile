package com.example.nocturnabrew_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nocturnabrew_mobile.models.GenericResponse;
import com.example.nocturnabrew_mobile.network.RetrofitInstance;
import com.example.nocturnabrew_mobile.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private Button btnCreateAccount, btnBack;
    private EditText boxName, boxEmail, boxPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        boxName = findViewById(R.id.NameSignUp);
        boxEmail = findViewById(R.id.emailSignUp);
        boxPassword = findViewById(R.id.pswSignUp);
        btnCreateAccount = findViewById(R.id.buttonCreate);
        btnBack = findViewById(R.id.buttonBack);
        btnCreateAccount.setOnClickListener(v -> createAccount());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void createAccount (){
        User newuser = new User(boxName.getText().toString(), boxEmail.getText().toString(), boxPassword.getText().toString());
        Call<GenericResponse<User>> call = RetrofitInstance.getApiService().createUser(newuser);
        call.enqueue(new Callback<GenericResponse<User>>() {
            @Override
            public void onResponse(Call<GenericResponse<User>> call, Response<GenericResponse<User>> response) {
                if(response.isSuccessful()){
                    User createdUser = response.body().getValues();
                    Toast.makeText(SignUpActivity.this,
                            "Welcome: " + createdUser.getName(),
                            Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();

                }else{
                    String msg = "Server error: " + response.errorBody().toString();
                    Toast.makeText(SignUpActivity.this,
                            "Error: " + msg,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<User>> call, Throwable t) {
                Log.e("API", "Fallo la petición: " + t.getMessage());
            }
        });
    }
}