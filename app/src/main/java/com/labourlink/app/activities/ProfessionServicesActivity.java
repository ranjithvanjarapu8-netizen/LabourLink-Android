package com.labourlink.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.labourlink.app.R;
import com.labourlink.app.adapters.ServiceAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionServicesActivity extends AppCompatActivity {

    private ImageView professionIcon;
    private TextView professionName;
    private TextView professionDescription;
    private TextView dailyWage;
    private TextView totalServices;

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession_services);

        professionIcon = findViewById(R.id.professionIcon);
        professionName = findViewById(R.id.professionName);
        professionDescription = findViewById(R.id.professionDescription);
        dailyWage = findViewById(R.id.dailyWage);
        totalServices = findViewById(R.id.totalServices);

        recyclerView = findViewById(R.id.recyclerServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Receive data from previous activity
        String profession =
                getIntent().getStringExtra("profession");

        String description =
                getIntent().getStringExtra("description");

        double wage =
                getIntent().getDoubleExtra("dailyWage", 0);

        // Show profession details
        professionName.setText(profession);

        if (description != null)
            professionDescription.setText(description);

        dailyWage.setText("₹" + wage + "/Day");

        String token =
                getSharedPreferences("LabourLink", MODE_PRIVATE)
                        .getString("token", "");

        apiService = ApiClient.getClient().create(ApiService.class);

        loadServices(profession, token);
    }

    private void loadServices(String profession, String token) {

        apiService.getProfessionServices(
                profession,
                "Bearer " + token
        ).enqueue(new Callback<List<Service>>() {

            @Override
            public void onResponse(Call<List<Service>> call,
                                   Response<List<Service>> response) {

                if (!response.isSuccessful()) {

                    Toast.makeText(
                            ProfessionServicesActivity.this,
                            "Unable to load services",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                List<Service> services = response.body();

                if (services == null || services.isEmpty()) {

                    totalServices.setText("0");

                    Toast.makeText(
                            ProfessionServicesActivity.this,
                            "No services found",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                totalServices.setText(
                        String.valueOf(services.size()));

                adapter = new ServiceAdapter(services);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Service>> call,
                                  Throwable t) {

                Toast.makeText(
                        ProfessionServicesActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}