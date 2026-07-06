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
import com.labourlink.app.adapters.WorkerRequestAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.IncomingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerRequests;
    private TextView txtRequestCount;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_requests);

        recyclerRequests = findViewById(R.id.recyclerRequests);
        txtRequestCount = findViewById(R.id.txtRequestCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerRequests.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadRequests();
    }

    private void loadRequests() {

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getIncomingRequests("Bearer " + token)
                .enqueue(new Callback<List<IncomingRequest>>() {

                    @Override
                    public void onResponse(Call<List<IncomingRequest>> call,
                                           Response<List<IncomingRequest>> response) {

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            List<IncomingRequest> requests =
                                    response.body();

                            txtRequestCount.setText(
                                    String.valueOf(requests.size())
                            );

                            if (requests.isEmpty()) {

                                layoutEmpty.setVisibility(View.VISIBLE);
                                recyclerRequests.setVisibility(View.GONE);

                            } else {

                                layoutEmpty.setVisibility(View.GONE);
                                recyclerRequests.setVisibility(View.VISIBLE);

                                WorkerRequestAdapter adapter =
                                        new WorkerRequestAdapter(
                                                WorkerRequestsActivity.this,
                                                requests
                                        );

                                recyclerRequests.setAdapter(adapter);

                            }

                        } else {

                            Toast.makeText(
                                    WorkerRequestsActivity.this,
                                    "Unable to load requests",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<List<IncomingRequest>> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkerRequestsActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}