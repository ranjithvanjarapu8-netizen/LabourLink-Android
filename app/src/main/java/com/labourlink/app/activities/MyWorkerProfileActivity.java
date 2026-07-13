package com.labourlink.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.Worker;
import android.content.Intent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.labourlink.app.models.Profession;
import com.google.android.material.chip.Chip;
import com.bumptech.glide.Glide;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MyWorkerProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;

    private TextView txtName;
    private TextView txtProfession;
    private TextView txtRating;
    private TextView txtJobs;
    private TextView txtExperience;

    private TextView txtDescription;

    private TextView txtPhone;
    private TextView txtLanguages;
    private TextView txtJoined;

    private TextView txtCity;
    private TextView txtDistrict;
    private TextView txtState;

    private ChipGroup chipSkills;

    private MaterialButton btnEditProfile;
    private MaterialButton btnAcceptedWorks;
    private MaterialButton btnMyRequests;
    private MaterialButton btnCompletedWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_worker_profile);

        imgProfile = findViewById(R.id.imgProfile);

        txtName = findViewById(R.id.txtName);
        txtProfession = findViewById(R.id.txtProfession);
        txtRating = findViewById(R.id.txtRating);
        txtJobs = findViewById(R.id.txtJobs);
        txtExperience = findViewById(R.id.txtExperience);

        txtDescription = findViewById(R.id.txtDescription);

        txtPhone = findViewById(R.id.txtPhone);
        txtLanguages = findViewById(R.id.txtLanguages);
        txtJoined = findViewById(R.id.txtJoined);

        txtCity = findViewById(R.id.txtCity);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtState = findViewById(R.id.txtState);

        chipSkills = findViewById(R.id.chipSkills);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnAcceptedWorks = findViewById(R.id.btnAcceptedWorks);
        btnMyRequests = findViewById(R.id.btnMyRequests);
        btnCompletedWorks = findViewById(R.id.btnCompletedWorks);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnAcceptedWorks = findViewById(R.id.btnAcceptedWorks);
        btnMyRequests = findViewById(R.id.btnMyRequests);
        btnCompletedWorks = findViewById(R.id.btnCompletedWorks);

// Accepted Works button
        btnAcceptedWorks.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MyWorkerProfileActivity.this,
                    WorkerAcceptedActivity.class
            );

            startActivity(intent);

        });
        btnCompletedWorks.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            MyWorkerProfileActivity.this,
                            CompletedJobsActivity.class
                    )
            );

        });
        btnMyRequests.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            MyWorkerProfileActivity.this,
                            WorkerRequestsActivity.class
                    )
            );

        });
        loadProfile();
    }

    private void loadProfile() {

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token = preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getMyWorkerProfile("Bearer " + token)
                .enqueue(new Callback<Worker>() {

                    @Override
                    public void onResponse(Call<Worker> call,
                                           Response<Worker> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            displayProfile(response.body());
                        } else {
                            Toast.makeText(
                                    MyWorkerProfileActivity.this,
                                    "Unable to load profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Worker> call, Throwable t) {

                        Toast.makeText(
                                MyWorkerProfileActivity.this,
                                "Server Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void displayProfile(Worker worker) {

        txtName.setText(worker.getName());
        txtPhone.setText(worker.getPhoneNumber());

        txtRating.setText(String.valueOf(worker.getRating()));
        txtJobs.setText(String.valueOf(worker.getTotalJobs()));
        txtExperience.setText(String.valueOf(worker.getExperience()));

        txtLanguages.setText(worker.getLanguages());
        txtDescription.setText(worker.getDescription());
        txtJoined.setText(formatDate(worker.getCreatedAt()));
        txtCity.setText(worker.getCity());
        txtDistrict.setText(worker.getDistrict());
        txtState.setText(worker.getState());
        chipSkills.removeAllViews();

        if (worker.getProfessions() != null) {

            StringBuilder builder = new StringBuilder();

            for (String profession : worker.getProfessions()) {

                if (builder.length() > 0) {
                    builder.append(" • ");
                }

                builder.append(profession);

                Chip chip = new Chip(this);

                chip.setText(profession);

                chip.setClickable(false);
                chip.setCheckable(false);

                chipSkills.addView(chip);
            }

            txtProfession.setText(builder.toString());

        } else {

            txtProfession.setText("No Profession");

        }
        if (worker.getProfilePhoto() != null &&
                !worker.getProfilePhoto().isEmpty()) {
            Glide.with(this)
                    .load(worker.getProfilePhoto())
                    .circleCrop()
                    .into(imgProfile);

        } else {

            imgProfile.setImageResource(R.drawable.ic_person);

        }
    }
    private String formatDate(String date) {

        if (date == null || date.isEmpty()) {
            return "-";
        }

        String[] parts = date.split("-");

        if (parts.length != 3) {
            return date;
        }

        String[] months = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        int month = Integer.parseInt(parts[1]);

        return parts[2] + " " + months[month - 1] + " " + parts[0];
    }
}