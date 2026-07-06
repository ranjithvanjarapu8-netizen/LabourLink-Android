package com.labourlink.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.labourlink.app.R;
import com.labourlink.app.models.AcceptedRequest;

import java.util.List;

public class CompletedJobAdapter extends RecyclerView.Adapter<CompletedJobAdapter.ViewHolder> {

    private final Context context;
    private final List<AcceptedRequest> completedJobs;

    public CompletedJobAdapter(Context context,
                               List<AcceptedRequest> completedJobs) {
        this.context = context;
        this.completedJobs = completedJobs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_completed_job,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        AcceptedRequest job = completedJobs.get(position);

        holder.txtTitle.setText(job.getTitle());

        holder.txtOwner.setText("Owner : " + job.getOwnerName());

        holder.txtPhone.setText("Phone : " + job.getOwnerPhone());

        holder.txtProfession.setText("Profession : " + job.getProfession());

        holder.txtDescription.setText("Description : " + job.getDescription());

        holder.txtAddress.setText("Address : " + job.getAddress());

        holder.txtDistance.setText(
                "Distance : " +
                        String.format("%.1f km",
                                job.getDistance())
        );

        holder.txtDate.setText("Date : " + job.getWorkDate());

        holder.txtTime.setText(
                "Time : "
                        + job.getStartTime()
                        + " - "
                        + job.getEndTime()
        );

        holder.txtStatus.setText(job.getStatus());

        // Rating

        if (job.getRating() != null) {

            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.txtRatingValue.setVisibility(View.VISIBLE);

            holder.ratingBar.setRating(job.getRating());

            holder.txtRatingValue.setText(
                    job.getRating() + " / 5"
            );

            holder.txtRatingStatus.setText("Rated by Owner");

        } else {

            holder.ratingBar.setVisibility(View.GONE);
            holder.txtRatingValue.setVisibility(View.GONE);

            holder.txtRatingStatus.setText(
                    "Waiting for Owner Rating"
            );

        }

    }

    @Override
    public int getItemCount() {
        return completedJobs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle,
                txtOwner,
                txtPhone,
                txtProfession,
                txtDescription,
                txtAddress,
                txtDistance,
                txtDate,
                txtTime,
                txtStatus,
                txtRatingStatus;

        RatingBar ratingBar;
        TextView txtRatingValue;
        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtProfession = itemView.findViewById(R.id.txtProfession);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtRatingValue =
                    itemView.findViewById(R.id.txtRatingValue);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtRatingStatus = itemView.findViewById(R.id.txtRatingStatus);
        }
    }
}