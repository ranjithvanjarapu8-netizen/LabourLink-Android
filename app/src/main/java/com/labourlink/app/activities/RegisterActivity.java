package com.labourlink.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.labourlink.app.R;
import okhttp3.ResponseBody;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.PhoneRequest;
import com.labourlink.app.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }
    private String name;
    private String phoneNumber;
    private String password;
    private void registerUser() {

        name = etName.getText().toString().trim();
        phoneNumber = etPhone.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumber.startsWith("+91")) {
            phoneNumber = "+91" + phoneNumber;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.sendOtp(new PhoneRequest(phoneNumber))
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        try {

                            if (response.isSuccessful()) {

                                showOtpDialog();

                            } else {

                                String message = "Unable to send OTP";

                                if (response.errorBody() != null) {
                                    message = response.errorBody().string();
                                }

                                Toast.makeText(RegisterActivity.this,
                                        message,
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {

                            Toast.makeText(RegisterActivity.this,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toast.makeText(RegisterActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }
    private void showOtpDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_otp, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etOtp = view.findViewById(R.id.etOtp);
        Button btnVerify = view.findViewById(R.id.btnVerifyOtp);
        TextView tvResend = view.findViewById(R.id.tvResendOtp);

        btnVerify.setOnClickListener(v -> {

            String otp = etOtp.getText().toString().trim();

            if (otp.length() != 6) {
                Toast.makeText(this,
                        "Enter a valid OTP",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService =
                    ApiClient.getClient().create(ApiService.class);

            VerifyOtpRequest request =
                    new VerifyOtpRequest(
                            name,
                            phoneNumber,
                            password,
                            otp
                    );

            apiService.verifyOtp(request)
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {

                            try {

                                String message = response.body() != null
                                        ? response.body().string()
                                        : response.errorBody().string();

                                Toast.makeText(RegisterActivity.this,
                                        message,
                                        Toast.LENGTH_LONG).show();

                                if (response.isSuccessful()) {

                                    dialog.dismiss();

                                    finish();

                                }

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call,
                                              Throwable t) {

                            Toast.makeText(RegisterActivity.this,
                                    t.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }

                    });

        });

        tvResend.setOnClickListener(v -> {

            ApiService apiService =
                    ApiClient.getClient().create(ApiService.class);

            apiService.sendOtp(new PhoneRequest(phoneNumber))
                    .enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {

                            Toast.makeText(RegisterActivity.this,
                                    "OTP Sent Again",
                                    Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call,
                                              Throwable t) {

                            Toast.makeText(RegisterActivity.this,
                                    t.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    });

        });

    }
}