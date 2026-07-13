package com.labourlink.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labourlink.app.R;
import com.labourlink.app.models.OwnerRequest;

import java.util.List;
import android.content.Intent;
import android.widget.Button;

import com.labourlink.app.activities.LiveTrackingActivity;
public class OwnerRequestAdapter extends RecyclerView.Adapter<OwnerRequestAdapter.ViewHolder> {

    private Context context;
    private List<OwnerRequest> requestList;


    public OwnerRequestAdapter(Context context,
                               List<OwnerRequest> requestList) {

        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_owner_request,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        OwnerRequest request = requestList.get(position);

        holder.txtWorkerName.setText(request.getWorkerName());

        holder.txtProfession.setText(request.getProfession());

        holder.txtTitle.setText(request.getTitle());

        holder.txtDate.setText(
                "Date : " + request.getWorkDate());

        holder.txtTime.setText(
                request.getStartTime()
                        + " - "
                        + request.getEndTime());

        holder.txtAddress.setText(request.getAddress());

        if ("ACCEPTED".equals(request.getStatus())) {

            holder.txtPhone.setVisibility(View.VISIBLE);

            holder.txtPhone.setText(
                    "Phone : " + request.getPhoneNumber());

            holder.btnTrackWorker.setVisibility(View.VISIBLE);

            holder.btnTrackWorker.setOnClickListener(v -> {

                Intent intent = new Intent(
                        context,
                        LiveTrackingActivity.class
                );

                intent.putExtra(
                        "requestId",
                        request.getRequestId()
                );

                context.startActivity(intent);

            });

        } else {

            holder.txtPhone.setVisibility(View.GONE);

            holder.btnTrackWorker.setVisibility(View.GONE);

        }

        holder.txtStatus.setText(request.getStatus());

        switch (request.getStatus()) {

            case "PENDING":

                holder.txtStatus.setBackgroundResource(
                        R.drawable.status_pending_bg);

                break;

            case "ACCEPTED":

                holder.txtStatus.setBackgroundResource(
                        R.drawable.status_accepted_bg);

                break;

            case "REJECTED":

                holder.txtStatus.setBackgroundResource(
                        R.drawable.status_rejected_bg);

                break;

            case "COMPLETED":

                holder.txtStatus.setBackgroundResource(
                        R.drawable.status_completed_bg);

                break;

        }

        if (request.getProfilePhoto() != null &&
                !request.getProfilePhoto().isEmpty()) {

            Glide.with(context)
                    .load(request.getProfilePhoto())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgWorker);
            

        } else {

            holder.imgWorker.setImageResource(
                    R.mipmap.ic_launcher);

        }

    }

    @Override
    public int getItemCount() {

        return requestList.size();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgWorker;

        TextView txtWorkerName;
        TextView txtProfession;
        TextView txtTitle;
        TextView txtDate;
        TextView txtTime;
        TextView txtAddress;
        TextView txtPhone;
        TextView txtStatus;
        Button btnTrackWorker;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imgWorker = itemView.findViewById(R.id.imgWorker);

            txtWorkerName = itemView.findViewById(R.id.txtWorkerName);

            txtProfession = itemView.findViewById(R.id.txtProfession);

            txtTitle = itemView.findViewById(R.id.txtTitle);

            txtDate = itemView.findViewById(R.id.txtDate);

            txtTime = itemView.findViewById(R.id.txtTime);

            txtAddress = itemView.findViewById(R.id.txtAddress);

            txtPhone = itemView.findViewById(R.id.txtPhone);

            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnTrackWorker = itemView.findViewById(R.id.btnTrackWorker);

        }

    }

}