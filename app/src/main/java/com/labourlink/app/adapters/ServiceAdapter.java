package com.labourlink.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.labourlink.app.R;
import com.labourlink.app.models.Service;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final List<Service> services;

    public ServiceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

        Service service = services.get(position);

        holder.txtServiceName.setText(service.getName());

        holder.txtPrice.setText("₹" + service.getPrice());

        holder.txtHours.setText(
                "Estimated Time : "
                        + service.getEstimatedHours()
                        + (service.getEstimatedHours() == 1 ? " Hour" : " Hours")
        );

        holder.txtDescription.setText(service.getDescription());

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView txtServiceName;
        TextView txtPrice;
        TextView txtHours;
        TextView txtDescription;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtHours = itemView.findViewById(R.id.txtHours);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
