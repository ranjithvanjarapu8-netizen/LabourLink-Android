package com.labourlink.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
public class LocationHelper {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(activity);
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST
        );
    }

    public FusedLocationProviderClient getClient() {
        return fusedLocationClient;
    }

    public static int getPermissionRequestCode() {
        return LOCATION_PERMISSION_REQUEST;
    }
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
    }
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(LocationCallback callback) {

        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                callback.onLocationReceived(
                        location.getLatitude(),
                        location.getLongitude()
                );

            }

        });
    }
}