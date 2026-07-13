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
import androidx.core.content.ContextCompat;

import com.labourlink.app.services.LocationTrackingService;
import com.labourlink.app.utils.TrackingStatusManager;import com.labourlink.app.utils.LocationHelper;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {

    private MaterialCardView btnFindWorkers;
    private MaterialCardView btnFindWork;
    private Button btnProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocationHelper locationHelper = new LocationHelper(this);

        if (!locationHelper.hasLocationPermission()) {
            locationHelper.requestLocationPermission();
        }
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

                    TrackingStatusManager trackingManager =
                            new TrackingStatusManager(HomeActivity.this);

                    trackingManager.checkTrackingStatus(enabled -> {

                        if (enabled) {

                            Intent serviceIntent = new Intent(
                                    HomeActivity.this,
                                    LocationTrackingService.class
                            );

                            if (ContextCompat.checkSelfPermission(
                                    HomeActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED) {
                                Log.d("LabourLink", "About to start LocationTrackingService");
                                ContextCompat.startForegroundService(
                                        HomeActivity.this,
                                        serviceIntent
                                );

                            } else {

                                Toast.makeText(
                                        HomeActivity.this,
                                        "Please grant location permission first.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                ActivityCompat.requestPermissions(
                                        HomeActivity.this,
                                        new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        },
                                        LocationHelper.getPermissionRequestCode()
                                );
                            }

                        }

                        Intent intent = new Intent(
                                HomeActivity.this,
                                MyWorkerProfileActivity.class
                        );

                        startActivity(intent);

                    });

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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationHelper.getPermissionRequestCode()) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "Location permission granted",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this,
                        "Location permission is required for live tracking",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}