package com.labourlink.app.activities;
import com.google.android.material.button.MaterialButton;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.WorkerProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class WorkerProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private ImageView imgProfile;

    private TextView txtName;
    private TextView txtProfession;
    private TextView txtRating;
    private TextView txtDescription;
    private TextView txtExperience;
    private TextView txtJobs;
    private TextView txtLanguages;
    private TextView txtJoined;
    private TextView txtAddress;
    private TextView txtAvailability;

    private ChipGroup chipSkills;

    private MaterialButton btnHireWorker;

    private ApiService apiService;

    private String token;

    private Long workerId;
    private TextView txtDistance;
    private double latitude;
    private double longitude;

    private static final String IMAGE_BASE_URL =
            "http://10.12.91.182:8080/uploads/workers/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worker_profile);

        btnBack = findViewById(R.id.btnBack);

        imgProfile = findViewById(R.id.imgProfile);

        txtName = findViewById(R.id.txtName);
        txtProfession = findViewById(R.id.txtProfession);
        txtRating = findViewById(R.id.txtRating);
        txtDescription = findViewById(R.id.txtDescription);
        txtExperience = findViewById(R.id.txtExperience);
        txtDistance = findViewById(R.id.txtDistance);
        txtJobs = findViewById(R.id.txtJobs);
        txtLanguages = findViewById(R.id.txtLanguages);
        txtJoined = findViewById(R.id.txtJoined);
        txtAddress = findViewById(R.id.txtAddress);
        txtAvailability = findViewById(R.id.txtAvailability);

        chipSkills = findViewById(R.id.chipSkills);

        btnHireWorker = findViewById(R.id.btnHireWorker);

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = preferences.getString("token", "");

        apiService =
                ApiClient.getClient().create(ApiService.class);

        workerId =
                getIntent().getLongExtra("workerId",0);

        String selectedProfession =
                getIntent().getStringExtra("profession");

        String workDate =
                getIntent().getStringExtra("workDate");

        String selectedLocation =
                getIntent().getStringExtra("location");

        latitude =
                getIntent().getDoubleExtra("latitude", 0);

        longitude =
                getIntent().getDoubleExtra("longitude", 0);

        double distance = getIntent().getDoubleExtra("distance", 0);

        txtDistance.setText(
                String.format("Distance : %.1f km away", distance)
        );

        btnBack.setOnClickListener(v -> finish());

        loadWorkerProfile();
        btnHireWorker.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WorkerProfileActivity.this,
                    BookingActivity.class
            );

            intent.putExtra("workerId", workerId);

            intent.putExtra(
                    "workerName",
                    txtName.getText().toString()
            );

            // Profession selected in Hire Requirements
            intent.putExtra(
                    "profession",
                    selectedProfession
            );

            // Date selected in Hire Requirements
            intent.putExtra(
                    "workDate",
                    workDate
            );
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);

            // Location selected in Hire Requirements
            intent.putExtra(
                    "location",
                    selectedLocation
            );
            Toast.makeText(
                    this,
                    "Profession = " + selectedProfession +
                            "\nDate = " + workDate +
                            "\nLocation = " + selectedLocation,
                    Toast.LENGTH_LONG
            ).show();
            startActivity(intent);

        });

    }
    private void loadWorkerProfile() {

        apiService.getWorkerProfile(
                "Bearer " + token,
                workerId
        ).enqueue(new Callback<WorkerProfile>() {

            @Override
            public void onResponse(Call<WorkerProfile> call,
                                   Response<WorkerProfile> response) {

                if (response.isSuccessful() && response.body() != null) {

                    displayWorker(response.body());

                } else {

                    Toast.makeText(
                            WorkerProfileActivity.this,
                            "Unable to load profile",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<WorkerProfile> call,
                                  Throwable t) {

                btnHireWorker.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            WorkerProfileActivity.this,
                            BookingActivity.class
                    );

                    intent.putExtra("workerId", workerId);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);

                    startActivity(intent);

                });
            }

        });


    }
    private void displayWorker(WorkerProfile worker) {

        txtName.setText(worker.getName());

        if (worker.getProfessions() != null && !worker.getProfessions().isEmpty()) {

            txtProfession.setText(
                    android.text.TextUtils.join(" • ", worker.getProfessions())
            );

        } else {

            txtProfession.setText("Not Specified");

        }

        txtRating.setText("⭐ " + worker.getRating());

        txtDescription.setText(
                worker.getDescription() == null ?
                        "No description available."
                        : worker.getDescription()
        );

        txtExperience.setText(
                "Experience : " + worker.getExperience() + " Years"
        );

        txtJobs.setText(
                "Completed Jobs : " + worker.getTotalJobs()
        );

        txtLanguages.setText(
                "Languages : " + worker.getLanguages()
        );

        if (worker.getCreatedAt() != null &&
                worker.getCreatedAt().length() >= 10) {

            txtJoined.setText(
                    "Joined : " +
                            worker.getCreatedAt().substring(0, 10)
            );

        } else {

            txtJoined.setText("Joined : -");

        }

        txtAddress.setText(
                worker.getCity()
                        + ", "
                        + worker.getDistrict()
                        + ", "
                        + worker.getState()
        );

        txtAvailability.setText(
                worker.isAvailable() ?
                        "🟢 Available" :
                        "🔴 Busy"
        );

        if (worker.getProfilePhoto() != null &&
                !worker.getProfilePhoto().isEmpty()) {

            Glide.with(this)
                    .load(IMAGE_BASE_URL + worker.getProfilePhoto())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imgProfile);

        } else {

            imgProfile.setImageResource(R.mipmap.ic_launcher);

        }

        chipSkills.removeAllViews();

        if (worker.getProfessions() != null) {

            for (String skill : worker.getProfessions()) {

                Chip chip = new Chip(this);

                chip.setText(skill);

                chip.setClickable(true);

                chip.setCheckable(false);

                chip.setChipBackgroundColorResource(R.color.blue);

                chip.setTextColor(getResources().getColor(android.R.color.white));

                chip.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            WorkerProfileActivity.this,
                            ProfessionServicesActivity.class
                    );

                    intent.putExtra("profession", skill);

                    startActivity(intent);

                });

                chipSkills.addView(chip);

            }

        }

    }}