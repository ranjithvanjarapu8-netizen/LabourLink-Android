package com.labourlink.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.api.NominatimApi;
import com.labourlink.app.api.NominatimClient;
import com.labourlink.app.models.LocationResponse;
import com.labourlink.app.models.Profession;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HireRequirementsActivity extends AppCompatActivity {

    private Spinner spinnerProfession;
    private EditText etWorkDate;
    private EditText etLocation;
    private Button btnSearch;
    private Button btnContinue;
    private ImageButton btnBack;
    private TextView txtSelectedLocation;

    private MapView map;
    private Marker marker;

    private ApiService apiService;

    private final List<Profession> professionList = new ArrayList<>();

    private final Calendar calendar = Calendar.getInstance();

    private String token;

    private double latitude = 20.5937;
    private double longitude = 78.9629;
    private String selectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_hire_requirements);

        spinnerProfession = findViewById(R.id.spinnerProfession);
        etWorkDate = findViewById(R.id.etWorkDate);
        etLocation = findViewById(R.id.etLocation);
        btnSearch = findViewById(R.id.btnSearch);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        txtSelectedLocation = findViewById(R.id.txtSelectedLocation);

        map = findViewById(R.id.map);

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = preferences.getString("token", "");

        apiService = ApiClient.getClient().create(ApiService.class);

        loadProfessions();

        initializeMap();

        etWorkDate.setOnClickListener(v -> openDatePicker());

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> searchLocation());

        btnContinue.setOnClickListener(v -> {

            if (spinnerProfession.getSelectedItem() == null) {

                Toast.makeText(
                        HireRequirementsActivity.this,
                        "Please select a profession.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (etWorkDate.getText().toString().trim().isEmpty()) {

                Toast.makeText(
                        HireRequirementsActivity.this,
                        "Please select work date.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (selectedAddress == null || selectedAddress.isEmpty()
                    || latitude == 0 || longitude == 0) {

                Toast.makeText(
                        HireRequirementsActivity.this,
                        "Please search and select a valid location.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    HireRequirementsActivity.this,
                    FindWorkersActivity.class
            );

            Profession selectedProfession =
                    (Profession) spinnerProfession.getSelectedItem();

            intent.putExtra(
                    "profession",
                    selectedProfession.getName()
            );

            intent.putExtra(
                    "professionId",
                    selectedProfession.getId()
            );
            intent.putExtra(
                    "date",
                    etWorkDate.getText().toString()
            );

            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("location", selectedAddress);

            startActivity(intent);

        });

    }
    private void initializeMap() {

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);

        GeoPoint india = new GeoPoint(20.5937, 78.9629);

        map.getController().setZoom(5.0);
        map.getController().setCenter(india);

        marker = new Marker(map);
        marker.setPosition(india);
        marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
        );
        marker.setTitle("Selected Location");

        map.getOverlays().add(marker);

        map.invalidate();

    }

    private void searchLocation() {

        String location = etLocation.getText().toString().trim();

        if (location.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a location.",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        NominatimApi api =
                NominatimClient.getClient().create(NominatimApi.class);

        api.searchLocation(
                        "LabourLink",
                        location,
                        "json",
                        1
                )
                .enqueue(new Callback<List<LocationResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<LocationResponse>> call,
                            Response<List<LocationResponse>> response) {
                        System.out.println("Code : " + response.code());
                        System.out.println("Body : " + response.body());
                        System.out.println("Error : " + response.errorBody());
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {

                            LocationResponse result =
                                    response.body().get(0);

                            latitude =
                                    Double.parseDouble(result.getLat());

                            longitude =
                                    Double.parseDouble(result.getLon());

                            selectedAddress =
                                    result.getDisplayName();

                            etLocation.setText(selectedAddress);

                            txtSelectedLocation.setText(selectedAddress);

                            GeoPoint point =
                                    new GeoPoint(latitude, longitude);

                            map.getController().animateTo(point);

                            map.getController().setZoom(15.0);

                            marker.setPosition(point);

                            map.invalidate();

                        } else {

                            Toast.makeText(
                                    HireRequirementsActivity.this,
                                    "Location not found.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<List<LocationResponse>> call,
                            Throwable t) {

                        Toast.makeText(
                                HireRequirementsActivity.this,
                                "Unable to search location.",
                                Toast.LENGTH_SHORT
                        ).show();

                        t.printStackTrace();

                    }

                });

    }
    private void openDatePicker() {

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar c = Calendar.getInstance();

                    c.set(year, month, dayOfMonth);

                    String date = new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(c.getTime());

                    etWorkDate.setText(date);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.show();

    }

    private void loadProfessions() {

        apiService.getAllProfessions("Bearer " + token)
                .enqueue(new Callback<List<Profession>>() {

                    @Override
                    public void onResponse(
                            Call<List<Profession>> call,
                            Response<List<Profession>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            professionList.clear();

                            professionList.addAll(response.body());

                            ArrayAdapter<Profession> adapter =
                                    new ArrayAdapter<>(
                                            HireRequirementsActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            professionList
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spinnerProfession.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    HireRequirementsActivity.this,
                                    "Failed to load professions.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<List<Profession>> call,
                            Throwable t) {

                        Toast.makeText(
                                HireRequirementsActivity.this,
                                "Unable to connect to server.",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.onPause();
        }
    }

}