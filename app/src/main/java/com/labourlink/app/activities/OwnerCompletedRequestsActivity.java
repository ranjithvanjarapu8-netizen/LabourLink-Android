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
import com.labourlink.app.adapters.OwnerCompletedRequestAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.OwnerCompletedRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerCompletedRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerCompleted;
    private TextView txtCompletedCount;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_completed_requests);

        recyclerCompleted = findViewById(R.id.recyclerCompleted);
        txtCompletedCount = findViewById(R.id.txtCompletedCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerCompleted.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadCompletedRequests();
    }

    private void loadCompletedRequests() {

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getOwnerCompletedRequests("Bearer " + token)
                .enqueue(new Callback<List<OwnerCompletedRequest>>() {

                    @Override
                    public void onResponse(Call<List<OwnerCompletedRequest>> call,
                                           Response<List<OwnerCompletedRequest>> response) {

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            List<OwnerCompletedRequest> requests =
                                    response.body();

                            txtCompletedCount.setText(
                                    String.valueOf(requests.size())
                            );

                            if (requests.isEmpty()) {

                                layoutEmpty.setVisibility(View.VISIBLE);
                                recyclerCompleted.setVisibility(View.GONE);

                            } else {

                                layoutEmpty.setVisibility(View.GONE);
                                recyclerCompleted.setVisibility(View.VISIBLE);

                                OwnerCompletedRequestAdapter adapter =
                                        new OwnerCompletedRequestAdapter(
                                                OwnerCompletedRequestsActivity.this,
                                                requests
                                        );

                                recyclerCompleted.setAdapter(adapter);

                            }

                        } else {

                            Toast.makeText(
                                    OwnerCompletedRequestsActivity.this,
                                    "Unable to load completed requests",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<List<OwnerCompletedRequest>> call,
                                          Throwable t) {

                        Toast.makeText(
                                OwnerCompletedRequestsActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}