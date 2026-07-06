package com.labourlink.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences preferences =
                    getSharedPreferences("LabourLink", MODE_PRIVATE);

            String token = preferences.getString("token", "");

            Intent intent;

            if (token == null || token.isEmpty()) {

                intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class);

            } else {

                intent = new Intent(
                        SplashActivity.this,
                        HomeActivity.class);

            }

            startActivity(intent);

            finish();

        }, 2000);

    }

}
