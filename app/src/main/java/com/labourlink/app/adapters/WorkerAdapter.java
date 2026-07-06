package com.labourlink.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labourlink.app.R;
import com.labourlink.app.activities.WorkerProfileActivity;
import com.labourlink.app.models.NearbyWorker;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private Context context;
    private List<NearbyWorker> workerList;

    private String profession;
    private String workDate;
    private String location;

    private double latitude;
    private double longitude;

    // Change this to your server IP
    private static final String IMAGE_BASE_URL =
            "http://10.12.91.182:8080/uploads/";

    public WorkerAdapter(
            Context context,
            List<NearbyWorker> workerList,
            String profession,
            String workDate,
            String location,
            double latitude,
            double longitude
    ) {

        this.context = context;
        this.workerList = workerList;

        this.profession = profession;
        this.workDate = workDate;
        this.location = location;

        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_worker, parent, false);

        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder,
                                 int position) {

        NearbyWorker worker = workerList.get(position);

        holder.txtName.setText(worker.getName());

        holder.txtProfession.setText(
                android.text.TextUtils.join(", ", worker.getProfession())
        );

        holder.txtExperience.setText(
                worker.getExperience() + " Years Experience");

        holder.txtDistance.setText(
                String.format("%.1f km away", worker.getDistance()));

        holder.txtRating.setText(
                "⭐ " + worker.getRating());

        holder.txtWage.setText(
                "₹" + worker.getDailyWage() + " / day");

        Glide.with(context)
                .load(IMAGE_BASE_URL + worker.getProfilePhoto())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgWorker);

        holder.btnViewProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    WorkerProfileActivity.class
            );

            intent.putExtra("workerId", worker.getWorkerId());

            intent.putExtra("distance", worker.getDistance());

            intent.putExtra("profession", profession);

            intent.putExtra("workDate", workDate);

            intent.putExtra("location", location);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);

            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    static class WorkerViewHolder extends RecyclerView.ViewHolder {

        ImageView imgWorker;

        TextView txtName;
        TextView txtProfession;
        TextView txtExperience;
        TextView txtDistance;
        TextView txtRating;
        TextView txtWage;

        Button btnViewProfile;

        public WorkerViewHolder(@NonNull View itemView) {

            super(itemView);

            imgWorker = itemView.findViewById(R.id.imgWorker);

            txtName = itemView.findViewById(R.id.txtName);
            txtProfession = itemView.findViewById(R.id.txtProfession);
            txtExperience = itemView.findViewById(R.id.txtExperience);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtWage = itemView.findViewById(R.id.txtWage);

            btnViewProfile =
                    itemView.findViewById(R.id.btnViewProfile);

        }

    }

}