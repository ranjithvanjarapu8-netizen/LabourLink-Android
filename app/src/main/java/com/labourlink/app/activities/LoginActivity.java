package com.labourlink.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.LoginRequest;
import com.labourlink.app.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, password;
    private Button loginButton;
    private TextView register, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.loginButton);

        register = findViewById(R.id.register);

        forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(v -> loginUser());
        loginButton.setOnClickListener(v -> loginUser());

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,
                    ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }

    private void loginUser() {

        String phoneNumber = phone.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (phoneNumber.length() != 10) {

            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
            return;

        }

        if (pass.isEmpty()) {

            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;

        }

        phoneNumber = "+91" + phoneNumber;

        LoginRequest request = new LoginRequest(phoneNumber, pass);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    SharedPreferences preferences =
                            getSharedPreferences("LabourLink", MODE_PRIVATE);

                    preferences.edit()
                            .putString("token", response.body().getToken())
                            .apply();

                    Toast.makeText(LoginActivity.this,
                            response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this,
                            HomeActivity.class));

                    finish();

                } else {

                    Toast.makeText(LoginActivity.this,
                            "Invalid phone or password",
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                t.printStackTrace();

                Toast.makeText(LoginActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }

        });

    }

}