package com.labourlink.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.LiveLocationDto;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.Marker;

import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveTrackingActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView txtLastUpdated;

    private ApiService apiService;

    private String token;

    private Long requestId;

    private Marker workerMarker;
    private Marker workMarker;

    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_live_tracking);

        mapView = findViewById(R.id.map);
        txtLastUpdated = findViewById(R.id.txtLastUpdated);

        mapView.setMultiTouchControls(true);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = "Bearer " + preferences.getString("token", "");

        requestId = getIntent().getLongExtra("requestId", -1);

        if (requestId == -1) {

            Toast.makeText(
                    this,
                    "Invalid Request",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        loadLiveLocation();

        handler.postDelayed(refreshRunnable, 20000);
    }
    private void loadLiveLocation() {

        apiService.getLiveLocation(token, requestId)
                .enqueue(new Callback<LiveLocationDto>() {

                    @Override
                    public void onResponse(Call<LiveLocationDto> call,
                                           Response<LiveLocationDto> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            updateMap(response.body());

                        }

                    }

                    @Override
                    public void onFailure(Call<LiveLocationDto> call,
                                          Throwable t) {

                        Toast.makeText(
                                LiveTrackingActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    private void updateMap(LiveLocationDto dto) {

        txtLastUpdated.setText(
                "Last Updated : " + dto.getLastUpdated()
        );

        GeoPoint workerPoint = new GeoPoint(
                dto.getWorkerLatitude(),
                dto.getWorkerLongitude()
        );

        GeoPoint workPoint = new GeoPoint(
                dto.getWorkLatitude(),
                dto.getWorkLongitude()
        );

        // Work Location Marker (only once)
        if (workMarker == null) {

            workMarker = new Marker(mapView);
            workMarker.setPosition(workPoint);
            workMarker.setTitle("Work Location");
            workMarker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
            );

            mapView.getOverlays().add(workMarker);
        }

        // Worker Marker (create once)
        if (workerMarker == null) {

            workerMarker = new Marker(mapView);
            workerMarker.setTitle("Worker");
            workerMarker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
            );

            mapView.getOverlays().add(workerMarker);
        }

        // Update worker position
        workerMarker.setPosition(workerPoint);

        MapController controller =
                (MapController) mapView.getController();

        controller.setZoom(17.0);
        controller.setCenter(workerPoint);

        mapView.invalidate();
    }
    private final Runnable refreshRunnable = new Runnable() {

        @Override
        public void run() {

            loadLiveLocation();

            handler.postDelayed(this, 20000);

        }
    };
    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacks(refreshRunnable);

        if (mapView != null) {
            mapView.onDetach();
        }
    }

}