package com.labourlink.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.labourlink.app.R;
import com.labourlink.app.adapters.WorkerAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.NearbyWorker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindWorkersActivity extends AppCompatActivity {

    private TextView txtProfession, txtDate, txtLocation;
    private Button btnChangeSearch;
    private RecyclerView recyclerWorkers;

    private WorkerAdapter adapter;
    private final List<NearbyWorker> workerList = new ArrayList<>();

    private ApiService apiService;
    private String token;

    private double latitude;
    private double longitude;
    private String profession;
    private String date;
    private String location;
    private Long professionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_workers);

        txtProfession = findViewById(R.id.txtProfession);
        txtDate = findViewById(R.id.txtDate);
        txtLocation = findViewById(R.id.txtLocation);
        btnChangeSearch = findViewById(R.id.btnChangeSearch);
        recyclerWorkers = findViewById(R.id.recyclerWorkers);

        SharedPreferences prefs = getSharedPreferences("LabourLink", MODE_PRIVATE);
        token = prefs.getString("token", "");

        apiService = ApiClient.getClient().create(ApiService.class);

        // Recycler setup
        // Get data from previous screen
        Intent intent = getIntent();

        profession = intent.getStringExtra("profession");
        professionId = intent.getLongExtra("professionId", 0);
        date = intent.getStringExtra("date");
        location = intent.getStringExtra("location");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

// Recycler setup
        recyclerWorkers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorkerAdapter(
                this,
                workerList,
                professionId,
                profession,
                date,
                location,
                latitude,
                longitude
        );

        recyclerWorkers.setAdapter(adapter);

// Set UI
        txtProfession.setText("Profession : " + profession);
        txtDate.setText("Work Date : " + date);
        txtLocation.setText("Location : " + location);

        btnChangeSearch.setOnClickListener(v -> finish());

        // API CALL
        loadWorkers();
    }

    private void loadWorkers() {

        apiService.getNearbyWorkers(
                        "Bearer " + token,
                        latitude,
                        longitude,
                        profession,
                        date
                )
                .enqueue(new Callback<List<NearbyWorker>>() {

                    @Override
                    public void onResponse(Call<List<NearbyWorker>> call,
                                           Response<List<NearbyWorker>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            workerList.clear();
                            workerList.addAll(response.body());

                            adapter.notifyDataSetChanged();

                            Log.d("WORKERS", "Count: " + workerList.size());

                            if (workerList.isEmpty()) {
                                Toast.makeText(FindWorkersActivity.this,
                                        "No workers found",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(FindWorkersActivity.this,
                                    "Failed to load workers",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NearbyWorker>> call, Throwable t) {

                        Toast.makeText(FindWorkersActivity.this,
                                "Server error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        Log.e("WORKERS_ERROR", t.getMessage(), t);
                    }
                });
    }
}