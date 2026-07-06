package com.labourlink.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.labourlink.app.models.AcceptedRequest;

import java.util.List;

public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<AcceptedRequest> requestList;

    public AcceptedRequestAdapter(Context context,
                                  List<AcceptedRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_accepted_request,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        AcceptedRequest request = requestList.get(position);

        holder.txtTitle.setText(request.getTitle());

        holder.txtOwner.setText("Owner : " + request.getOwnerName());

        holder.txtPhone.setText("Phone : " + request.getOwnerPhone());

        holder.txtProfession.setText("Profession : " + request.getProfession());

        holder.txtDescription.setText("Description : " + request.getDescription());

        holder.txtAddress.setText("Address : " + request.getAddress());

        holder.txtDistance.setText(
                "Distance : " +
                        String.format("%.1f km",
                                request.getDistance())
        );

        holder.txtDate.setText("Date : " + request.getWorkDate());

        holder.txtTime.setText(
                "Time : "
                        + request.getStartTime()
                        + " - "
                        + request.getEndTime()
        );

        holder.txtStatus.setText(request.getStatus());

        holder.btnMap.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                            "https://maps.google.com/?q="
                                    + request.getLatitude()
                                    + ","
                                    + request.getLongitude()
                    )
            );

            context.startActivity(intent);

        });

        holder.btnDetails.setOnClickListener(v -> {

            // Next screen
            // WorkerRequestDetailsActivity

        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
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
                txtStatus;

        MaterialButton btnDetails,
                btnMap;

        public ViewHolder(@NonNull View itemView) {
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

            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnMap = itemView.findViewById(R.id.btnMap);
        }
    }
}