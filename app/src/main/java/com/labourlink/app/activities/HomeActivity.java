package com.labourlink.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.Worker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class HomeActivity extends AppCompatActivity {

    private MaterialCardView btnFindWorkers;
    private MaterialCardView btnFindWork;
    private Button btnProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token = preferences.getString("token", null);

        if (token == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnFindWorkers = findViewById(R.id.btnFindWorkers);
        btnFindWork = findViewById(R.id.btnFindWork);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Find Workers
        btnFindWorkers.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    HireRequirementsActivity.class
            );

            startActivity(intent);

        });

        // Find Work
        // Find Work
        btnFindWork.setOnClickListener(v -> checkWorkerProfile());
        // Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    OwnerProfileActivity.class
            );

            startActivity(intent);

        });

        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    private void checkWorkerProfile() {

        String token = getSharedPreferences("LabourLink", MODE_PRIVATE)
                .getString("token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Worker> call = apiService.getMyWorkerProfile("Bearer " + token);

        call.enqueue(new Callback<Worker>() {

            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {

                Toast.makeText(
                        HomeActivity.this,
                        "Response Code : " + response.code(),
                        Toast.LENGTH_LONG
                ).show();

                if (response.isSuccessful()) {

                    Intent intent = new Intent(
                            HomeActivity.this,
                            MyWorkerProfileActivity.class
                    );
                    startActivity(intent);

                } else if (response.code() == 404) {

                    Intent intent = new Intent(
                            HomeActivity.this,
                            WorkerRegisterActivity.class
                    );
                    startActivity(intent);

                } else {

                    Toast.makeText(
                            HomeActivity.this,
                            "Error : " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();

                }
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {

                t.printStackTrace();

                Toast.makeText(
                        HomeActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }
        });
    }
    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {

                    SharedPreferences preferences =
                            getSharedPreferences("LabourLink", MODE_PRIVATE);

                    preferences.edit().clear().apply();

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}