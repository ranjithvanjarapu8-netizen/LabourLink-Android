package com.labourlink.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.OwnerProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private TextView txtName;
    private TextView txtPhone;
    private TextView txtJoined;

    private TextView txtTotalRequests;
    private TextView txtUpcomingRequests;
    private TextView txtPastRequests;

    private MaterialButton btnFindWorkers;
    private MaterialButton btnMyRequests;
    private MaterialButton btnCompletedRequests;
    private MaterialButton btnLogout;
    private ApiService apiService;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner_profile);

        initViews();

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        token = preferences.getString("token", "");

        apiService =
                ApiClient.getClient().create(ApiService.class);

        loadProfile();

        btnBack.setOnClickListener(v -> finish());

        btnFindWorkers.setOnClickListener(v -> {

            startActivity(new Intent(
                    OwnerProfileActivity.this,
                    HireRequirementsActivity.class));

        });

        btnMyRequests.setOnClickListener(v -> {

            startActivity(new Intent(
                    OwnerProfileActivity.this,
                    OwnerRequestsActivity.class));

        });

        btnCompletedRequests.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            OwnerProfileActivity.this,
                            OwnerCompletedRequestsActivity.class
                    )
            );

        });

        btnLogout.setOnClickListener(v -> {

            SharedPreferences.Editor editor =
                    preferences.edit();

            editor.clear();

            editor.apply();

            Intent intent = new Intent(
                    OwnerProfileActivity.this,
                    LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            finish();

        });

    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtJoined = findViewById(R.id.txtJoined);

        txtTotalRequests =
                findViewById(R.id.txtTotalRequests);

        txtUpcomingRequests =
                findViewById(R.id.txtUpcomingRequests);

        txtPastRequests =
                findViewById(R.id.txtPastRequests);

        btnFindWorkers =
                findViewById(R.id.btnFindWorkers);

        btnMyRequests =
                findViewById(R.id.btnMyRequests);
        btnCompletedRequests =
                findViewById(R.id.btnCompletedRequests);

        btnLogout =
                findViewById(R.id.btnLogout);

    }

    private void loadProfile() {

        apiService.getOwnerProfile(
                "Bearer " + token
        ).enqueue(new Callback<OwnerProfile>() {

            @Override
            public void onResponse(
                    Call<OwnerProfile> call,
                    Response<OwnerProfile> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    OwnerProfile profile =
                            response.body();

                    txtName.setText(profile.getName());

                    txtPhone.setText(
                            profile.getPhoneNumber());

                    if (profile.getJoinedDate() != null &&
                            profile.getJoinedDate().length() >= 10) {

                        txtJoined.setText(
                                "Joined : "
                                        + profile.getJoinedDate()
                                        .substring(0,10));

                    }

                    txtTotalRequests.setText(
                            String.valueOf(
                                    profile.getTotalRequests()));

                    txtUpcomingRequests.setText(
                            String.valueOf(
                                    profile.getUpcomingRequests()));

                    txtPastRequests.setText(
                            String.valueOf(
                                    profile.getPastRequests()));

                } else {

                    Toast.makeText(
                            OwnerProfileActivity.this,
                            "Unable to load profile",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(
                    Call<OwnerProfile> call,
                    Throwable t) {

                Toast.makeText(
                        OwnerProfileActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

}