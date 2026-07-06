package com.labourlink.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.labourlink.app.R;
import com.labourlink.app.adapters.CompletedJobAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.AcceptedRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedJobsActivity extends AppCompatActivity {

    private RecyclerView recyclerCompleted;
    private TextView txtCompletedCount;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_jobs);

        recyclerCompleted = findViewById(R.id.recyclerCompleted);
        txtCompletedCount = findViewById(R.id.txtCompletedCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerCompleted.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadCompletedJobs();
    }

    private void loadCompletedJobs() {

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getCompletedRequests("Bearer " + token)
                .enqueue(new Callback<List<AcceptedRequest>>() {

                    @Override
                    public void onResponse(Call<List<AcceptedRequest>> call,
                                           Response<List<AcceptedRequest>> response) {

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            List<AcceptedRequest> jobs =
                                    response.body();

                            txtCompletedCount.setText(
                                    String.valueOf(jobs.size())
                            );

                            if (jobs.isEmpty()) {

                                layoutEmpty.setVisibility(View.VISIBLE);
                                recyclerCompleted.setVisibility(View.GONE);

                            } else {

                                layoutEmpty.setVisibility(View.GONE);
                                recyclerCompleted.setVisibility(View.VISIBLE);

                                CompletedJobAdapter adapter =
                                        new CompletedJobAdapter(
                                                CompletedJobsActivity.this,
                                                jobs
                                        );

                                recyclerCompleted.setAdapter(adapter);

                            }

                        } else {

                            Toast.makeText(
                                    CompletedJobsActivity.this,
                                    "Unable to load completed jobs",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<List<AcceptedRequest>> call,
                                          Throwable t) {

                        Toast.makeText(
                                CompletedJobsActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }
}