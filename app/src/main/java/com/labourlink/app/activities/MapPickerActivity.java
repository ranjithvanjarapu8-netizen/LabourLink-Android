package com.labourlink.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.Marker;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.widget.Button;import org.osmdroid.api.IMapController;
import android.widget.ImageButton;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MapPickerActivity extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;
    private Marker marker;
    private EditText etSearch;
    private Button btnSearch;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtAddress;

    private double selectedLatitude;
    private double selectedLongitude;
    private Button btnSelect;
    private String selectedLocation = "";
    private String selectedCity = "";
    private String selectedDistrict = "";
    private String selectedState = "";
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            getCurrentLocation();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Location Permission Denied",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Initialize OSMDroid

        Configuration.getInstance().load(
                getApplicationContext(),
                androidx.preference.PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
        );

        setContentView(R.layout.activity_map_picker);

        txtAddress = findViewById(R.id.txtAddress);;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.map);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        btnSelect = findViewById(R.id.btnSelect);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Default location (Visakhapatnam)
        GeoPoint defaultLocation = new GeoPoint(17.6868, 83.2185);

        mapController.setCenter(defaultLocation);

        marker = new Marker(mapView);
        marker.setPosition(defaultLocation);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mapView.getOverlays().add(marker);
        mapView.invalidate();
        checkLocationPermission();
        btnSearch.setOnClickListener(v -> {

            String place = etSearch.getText().toString().trim();

            if (!place.isEmpty()) {

                hideKeyboard();

                searchLocation(place);

            }

        });
        MapEventsReceiver receiver = new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point) {

                marker.setPosition(point);

                mapView.invalidate();

                selectedLatitude = point.getLatitude();
                selectedLongitude = point.getLongitude();

                loadLocation(point);

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                return false;
            }
        };

        MapEventsOverlay overlay = new MapEventsOverlay(receiver);

        mapView.getOverlays().add(overlay);
        btnSelect.setOnClickListener(v -> {

            if (selectedLocation.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select a location",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent data = new Intent();

            data.putExtra("latitude", selectedLatitude);
            data.putExtra("longitude", selectedLongitude);

            data.putExtra("city", selectedCity);
            data.putExtra("district", selectedDistrict);
            data.putExtra("state", selectedState);

            data.putExtra("location", selectedLocation);

            setResult(RESULT_OK, data);

            finish();

        });
    }
    private void loadLocation(GeoPoint point) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses =
                    geocoder.getFromLocation(
                            point.getLatitude(),
                            point.getLongitude(),
                            1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                selectedCity = address.getLocality();
                selectedDistrict = address.getSubAdminArea();
                selectedState = address.getAdminArea();

                String area = address.getSubLocality();

                if (area == null || area.isEmpty()) {
                    area = address.getFeatureName();
                }

                StringBuilder builder = new StringBuilder();

                if (area != null && !area.isEmpty()) {
                    builder.append(area);
                }

                if (selectedCity != null && !selectedCity.isEmpty()) {

                    if (builder.length() > 0)
                        builder.append(", ");

                    builder.append(selectedCity);
                }

                if (selectedState != null && !selectedState.isEmpty()) {

                    if (builder.length() > 0)
                        builder.append(", ");

                    builder.append(selectedState);
                }

                selectedLocation = builder.toString();

                txtAddress.setText(selectedLocation);

            }

        } catch (IOException e) {

            e.printStackTrace();

            txtAddress.setText("Unable to fetch location");

        }

    }
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();

        } else {

            permissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);

        }

    }
    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                moveToLocation(location);

            } else {

                Toast.makeText(
                        this,
                        "Couldn't get your current location.",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }).addOnFailureListener(e ->

                Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show()

        );

    }
    private void moveToLocation(Location location) {

        GeoPoint point =
                new GeoPoint(
                        location.getLatitude(),
                        location.getLongitude());

        mapController.animateTo(point);

        marker.setPosition(point);

        selectedLatitude = point.getLatitude();
        selectedLongitude = point.getLongitude();

        loadLocation(point);

        mapView.invalidate();

    }
    private void searchLocation(String place) {

        Geocoder geocoder =
                new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses =
                    geocoder.getFromLocationName(place, 1);

            if (addresses == null || addresses.isEmpty()) {

                Toast.makeText(
                        this,
                        "Location not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Address address = addresses.get(0);

            GeoPoint point =
                    new GeoPoint(
                            address.getLatitude(),
                            address.getLongitude());

            mapController.animateTo(point);

            marker.setPosition(point);
            selectedLatitude = point.getLatitude();
            selectedLongitude = point.getLongitude();

            loadLocation(point);

            mapView.invalidate();

        }

        catch (IOException e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Search Failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }
    private void hideKeyboard() {

        View view = getCurrentFocus();

        if (view != null) {

            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}