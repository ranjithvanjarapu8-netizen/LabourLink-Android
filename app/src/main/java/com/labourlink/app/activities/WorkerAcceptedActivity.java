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
import com.labourlink.app.adapters.AcceptedRequestAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.AcceptedRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerAcceptedActivity extends AppCompatActivity {

    private RecyclerView recyclerAccepted;
    private TextView txtAcceptedCount;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_accepted);

        recyclerAccepted = findViewById(R.id.recyclerAccepted);
        txtAcceptedCount = findViewById(R.id.txtAcceptedCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerAccepted.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadAcceptedRequests();
    }

    private void loadAcceptedRequests() {

        SharedPreferences preferences =
                getSharedPreferences("LabourLink", MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getAcceptedRequests("Bearer " + token)
                .enqueue(new Callback<List<AcceptedRequest>>() {

                    @Override
                    public void onResponse(Call<List<AcceptedRequest>> call,
                                           Response<List<AcceptedRequest>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<AcceptedRequest> list = response.body();

                            txtAcceptedCount.setText(
                                    String.valueOf(list.size())
                            );

                            if (list.isEmpty()) {

                                layoutEmpty.setVisibility(View.VISIBLE);
                                recyclerAccepted.setVisibility(View.GONE);

                            } else {

                                layoutEmpty.setVisibility(View.GONE);
                                recyclerAccepted.setVisibility(View.VISIBLE);

                                AcceptedRequestAdapter adapter =
                                        new AcceptedRequestAdapter(
                                                WorkerAcceptedActivity.this,
                                                list
                                        );

                                recyclerAccepted.setAdapter(adapter);
                            }

                        } else {

                            Toast.makeText(
                                    WorkerAcceptedActivity.this,
                                    "Unable to load accepted jobs",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<List<AcceptedRequest>> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkerAcceptedActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}