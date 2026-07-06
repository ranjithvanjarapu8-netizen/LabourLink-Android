package com.labourlink.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.PhoneRequest;
import com.labourlink.app.models.VerifyForgotOtpRequest;
import com.labourlink.app.models.ResetPasswordRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private LinearLayout step1, step2, step3;

    private EditText etPhone;

    private Button btnSendOtp;

    private String phoneNumber;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);

        etPhone = findViewById(R.id.etPhone);

        btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> sendOtp());
        etOtp = findViewById(R.id.etOtp);

        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResendOtp = findViewById(R.id.btnResendOtp);

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        btnResendOtp.setOnClickListener(v -> resendOtp());
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> resetPassword());

    }
    private EditText etOtp;
    private Button btnVerifyOtp;
    private Button btnResendOtp;

    private void sendOtp() {

        phoneNumber = etPhone.getText().toString().trim();

        if (phoneNumber.length() != 10) {

            Toast.makeText(this,
                    "Enter valid mobile number",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        phoneNumber = "+91" + phoneNumber;

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.sendForgotOtp(new PhoneRequest(phoneNumber))
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        try {

                            String message = response.isSuccessful()
                                    ? response.body().string()
                                    : response.errorBody().string();

                            Toast.makeText(ForgotPasswordActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();

                            if (response.isSuccessful()) {

                                step1.setVisibility(View.GONE);
                                step2.setVisibility(View.VISIBLE);

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {

                        Toast.makeText(ForgotPasswordActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }

    private void verifyOtp() {

        String otp = etOtp.getText().toString().trim();

        if (otp.length() != 6) {

            Toast.makeText(this,
                    "Enter a valid OTP",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        VerifyForgotOtpRequest request =
                new VerifyForgotOtpRequest(phoneNumber, otp);

        apiService.verifyForgotOtp(request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        try {

                            String message = response.isSuccessful()
                                    ? response.body().string()
                                    : response.errorBody().string();

                            Toast.makeText(ForgotPasswordActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();

                            if (response.isSuccessful()) {

                                step2.setVisibility(View.GONE);
                                step3.setVisibility(View.VISIBLE);

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {

                        Toast.makeText(ForgotPasswordActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }
    private void resendOtp() {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.sendForgotOtp(new PhoneRequest(phoneNumber))
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        try {

                            String message = response.isSuccessful()
                                    ? response.body().string()
                                    : response.errorBody().string();

                            Toast.makeText(ForgotPasswordActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {

                        Toast.makeText(ForgotPasswordActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }
    private void resetPassword() {

        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.length() < 6) {

            Toast.makeText(this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            Toast.makeText(this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        ResetPasswordRequest request =
                new ResetPasswordRequest(phoneNumber, newPassword);

        apiService.resetPassword(request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        try {

                            String message = response.isSuccessful()
                                    ? response.body().string()
                                    : response.errorBody().string();

                            Toast.makeText(ForgotPasswordActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        ForgotPasswordActivity.this,
                                        "Password Reset Successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(ForgotPasswordActivity.this,
                                        LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }
}