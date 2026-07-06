package com.labourlink.app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.labourlink.app.R;
import com.labourlink.app.api.ApiClient;
import com.labourlink.app.api.ApiService;
import com.labourlink.app.models.IncomingRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerRequestAdapter
        extends RecyclerView.Adapter<WorkerRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<IncomingRequest> requestList;

    public WorkerRequestAdapter(Context context,
                                List<IncomingRequest> requestList) {

        this.context = context;
        this.requestList = requestList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_worker_request,
                        parent,
                        false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        IncomingRequest request = requestList.get(position);

        holder.txtTitle.setText(request.getTitle());

        holder.txtOwner.setText(
                "Owner : " + request.getOwnerName());

        holder.txtProfession.setText(
                "Profession : " + request.getProfession());

        holder.txtDescription.setText(
                "Description : " + request.getDescription());

        holder.txtAddress.setText(
                "Address : " + request.getAddress());

        holder.txtDistance.setText(
                "Distance : "
                        + String.format("%.1f km",
                        request.getDistance()));

        holder.txtDate.setText(
                "Date : " + request.getWorkDate());

        holder.txtTime.setText(
                "Time : "
                        + request.getStartTime()
                        + " - "
                        + request.getEndTime());

        holder.btnAccept.setOnClickListener(v ->
                updateRequest(
                        request.getRequestId(),
                        true,
                        position));

        holder.btnReject.setOnClickListener(v ->
                updateRequest(
                        request.getRequestId(),
                        false,
                        position));

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void updateRequest(Long requestId,
                               boolean accept,
                               int position) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "LabourLink",
                        Context.MODE_PRIVATE);

        String token =
                preferences.getString("token", "");

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        Call<ResponseBody> call;

        if (accept) {

            call = apiService.acceptRequest(
                    "Bearer " + token,
                    requestId);

        } else {

            call = apiService.rejectRequest(
                    "Bearer " + token,
                    requestId);

        }

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    Call<ResponseBody> call,
                    Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    requestList.remove(position);

                    notifyItemRemoved(position);

                    Toast.makeText(
                            context,
                            accept
                                    ? "Request Accepted"
                                    : "Request Rejected",
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
                        Toast.LENGTH_LONG
                ).show();

            }

        });

    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTitle,
                txtOwner,
                txtProfession,
                txtDescription,
                txtAddress,
                txtDistance,
                txtDate,
                txtTime;

        MaterialButton btnDetails,
                btnReject,
                btnAccept;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtProfession = itemView.findViewById(R.id.txtProfession);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);

            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);

        }

    }

}