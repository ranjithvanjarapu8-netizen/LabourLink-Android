package com.labourlink.app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.OwnerCompletedRequest;
import com.labourlink.app.models.WorkerRatingRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerCompletedRequestAdapter extends RecyclerView.Adapter<OwnerCompletedRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<OwnerCompletedRequest> requestList;

    public OwnerCompletedRequestAdapter(Context context,
                                        List<OwnerCompletedRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_owner_completed_request,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        OwnerCompletedRequest job = requestList.get(position);

        holder.txtTitle.setText(job.getTitle());

        holder.txtWorker.setText(
                "Worker : " + job.getWorkerName());

        holder.txtProfession.setText(
                "Profession : " + job.getProfession());

        holder.txtDescription.setText(
                "Description : " + job.getDescription());

        holder.txtAddress.setText(
                "Address : " + job.getAddress());

        holder.txtDate.setText(
                "Date : " + job.getWorkDate());

        holder.txtTime.setText(
                "Time : "
                        + job.getStartTime()
                        + " - "
                        + job.getEndTime());

        holder.txtStatus.setText(job.getStatus());

        if (job.getRating() == null) {

            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.btnSubmit.setVisibility(View.VISIBLE);
            holder.txtRated.setVisibility(View.GONE);

        } else {

            holder.ratingBar.setVisibility(View.GONE);
            holder.btnSubmit.setVisibility(View.GONE);

            holder.txtRated.setVisibility(View.VISIBLE);
            holder.txtRated.setText(
                    "Rated : ★ " + job.getRating() + "/5"
            );

        }

        holder.btnSubmit.setOnClickListener(v -> {

            submitRating(
                    job,
                    holder
            );

        });

    }

    private void submitRating(
            OwnerCompletedRequest job,
            ViewHolder holder) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "LabourLink",
                        Context.MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        WorkerRatingRequest request =
                new WorkerRatingRequest(
                        job.getRequestId(),
                        (int) holder.ratingBar.getRating()
                );

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.rateWorker(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    Call<ResponseBody> call,
                    Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    holder.ratingBar.setVisibility(View.GONE);
                    holder.btnSubmit.setVisibility(View.GONE);

                    holder.txtRated.setVisibility(View.VISIBLE);

                    holder.txtRated.setText(
                            "Rated : ★ "
                                    + request.getStars()
                                    + "/5"
                    );

                    Toast.makeText(
                            context,
                            "Rating Submitted",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }

            @Override
            public void onFailure(
                    Call<ResponseBody> call,
                    Throwable t) {

                Toast.makeText(
                        context,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();

            }

        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle,
                txtWorker,
                txtProfession,
                txtDescription,
                txtAddress,
                txtDate,
                txtTime,
                txtStatus,
                txtRated;

        RatingBar ratingBar;

        MaterialButton btnSubmit;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtWorker = itemView.findViewById(R.id.txtWorker);
            txtProfession = itemView.findViewById(R.id.txtProfession);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtRated = itemView.findViewById(R.id.txtRated);

            ratingBar = itemView.findViewById(R.id.ratingBar);

            btnSubmit = itemView.findViewById(R.id.btnSubmitRating);

        }

    }

}
