package com.labourlink.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.labourlink.app.models.LocationRequest;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import androidx.core.app.NotificationCompat;
public class LocationTrackingService extends Service {
    private static final String CHANNEL_ID = "LabourLinkTracking";
    private static final int NOTIFICATION_ID = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "LabourLink";
    private com.google.android.gms.location.LocationRequest gpsRequest;

    private LocationCallback locationCallback;

    private ApiService apiService;

    private String token;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = "Bearer " + preferences.getString("token", "");
    }
    private Notification createNotification() {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LabourLink")
                .setContentText("Sharing your work location")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();
    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Worker Location Tracking",
                    NotificationManager.IMPORTANCE_LOW
            );

            channel.setDescription("Tracks worker location during work.");

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started");
        startForeground(
                NOTIFICATION_ID,
                createNotification()
        );

        startLocationUpdates();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    private void startLocationUpdates() {
        Log.d(TAG, "Starting Location Updates");
        gpsRequest = new com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                20000 // 20 seconds
        )
                .setMinUpdateIntervalMillis(10000)
                .build();

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                Log.d(TAG,
                        "Received " + locationResult.getLocations().size() + " locations");
                for (Location location : locationResult.getLocations()) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Log.d(TAG,
                            "Latitude = " + latitude +
                                    ", Longitude = " + longitude);

                    sendLocationToServer(latitude, longitude);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            stopSelf();
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                gpsRequest,
                locationCallback,
                getMainLooper()
        );
    }
    private void sendLocationToServer(double latitude, double longitude) {

        LocationRequest request =
                new LocationRequest(latitude, longitude);
        Log.d(TAG, "Sending location to server...");
        apiService.updateLocation(token, request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        if (response.isSuccessful()) {

                            Log.d(TAG,
                                    "Location uploaded successfully");

                        } else {

                            Log.e(TAG,
                                    "Upload failed : " + response.code());

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call,
                                          Throwable t) {

                        Log.e(TAG,
                                "API Error : " + t.getMessage());

                    }
                });
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}