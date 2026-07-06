package com.labourlink.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.labourlink.app.adapters.OwnerRequestAdapter;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.OwnerRequest;
import com.labourlink.app.models.OwnerRequestsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;import com.labourlink.app.R;import com.google.android.material.chip.Chip;

public class OwnerRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView txtTotal;
    private TextView txtUpcoming;
    private TextView txtPast;



    private Chip chipAll;
    private Chip chipPending;
    private Chip chipAccepted;
    private Chip chipRejected;
    private Chip chipCompleted;

    private ApiService apiService;

    private String token;

    private OwnerRequestAdapter adapter;

    private final List<OwnerRequest> allRequests = new ArrayList<>();

    private final List<OwnerRequest> displayRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner_requests);

        recyclerView = findViewById(R.id.recyclerRequests);

        txtTotal = findViewById(R.id.txtTotal);

        txtUpcoming = findViewById(R.id.txtUpcoming);

        txtPast = findViewById(R.id.txtPast);

        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipAccepted = findViewById(R.id.chipAccepted);
        chipRejected = findViewById(R.id.chipRejected);
        chipCompleted = findViewById(R.id.chipCompleted);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new OwnerRequestAdapter(
                this,
                displayRequests
        );

        recyclerView.setAdapter(adapter);

        SharedPreferences preferences =
                getSharedPreferences(
                        "LabourLink",
                        MODE_PRIVATE
                );

        token = preferences.getString("token", "");

        apiService =
                ApiClient.getClient().create(ApiService.class);

        loadRequests();

        chipAll.setOnClickListener(v -> filter("ALL"));

        chipPending.setOnClickListener(v -> filter("PENDING"));

        chipAccepted.setOnClickListener(v -> filter("ACCEPTED"));

        chipRejected.setOnClickListener(v -> filter("REJECTED"));

        chipCompleted.setOnClickListener(v -> filter("COMPLETED"));

    }

    private void loadRequests() {

        apiService.getOwnerRequests(
                "Bearer " + token
        ).enqueue(new Callback<OwnerRequestsResponse>() {

            @Override
            public void onResponse(
                    Call<OwnerRequestsResponse> call,
                    Response<OwnerRequestsResponse> response) {

                if(response.isSuccessful()
                        && response.body()!=null){

                    OwnerRequestsResponse data =
                            response.body();

                    txtTotal.setText(
                            String.valueOf(
                                    data.getTotalRequests()));

                    txtUpcoming.setText(
                            String.valueOf(
                                    data.getUpcomingCount()));

                    txtPast.setText(
                            String.valueOf(
                                    data.getPastCount()));

                    allRequests.clear();

                    if(data.getUpcoming()!=null)
                        allRequests.addAll(data.getUpcoming());

                    if(data.getPast()!=null)
                        allRequests.addAll(data.getPast());

                    filter("ALL");

                }else{

                    Toast.makeText(
                            OwnerRequestsActivity.this,
                            "Unable to load requests",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(
                    Call<OwnerRequestsResponse> call,
                    Throwable t) {

                Toast.makeText(
                        OwnerRequestsActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

    private void filter(String status){

        displayRequests.clear();

        if(status.equals("ALL")){

            displayRequests.addAll(allRequests);

        }else{

            for(OwnerRequest request : allRequests){

                if(request.getStatus().equals(status)){

                    displayRequests.add(request);

                }

            }

        }

        adapter.notifyDataSetChanged();

    }

}