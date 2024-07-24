package com.example.primehomeservices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.RecentActivityViewHolder> {

    private List<OrdersClass> recentActivities;

    public RecentActivityAdapter(List<OrdersClass> recentActivities) {
        this.recentActivities = recentActivities;
    }

    @NonNull
    @Override
    public RecentActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recent, parent, false);
        return new RecentActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentActivityViewHolder holder, int position) {
        OrdersClass order = recentActivities.get(position);
        holder.activitySummaryTextView.setText("Pending service order");
        holder.activityDateTextView.setText(order.getDate());
        holder.activityTimeTextView.setText(order.getTime());
        holder.activityLocationTextView.setText(order.getLocation());
        // Optionally, bind other fields like grand total, item total, etc.
    }

    @Override
    public int getItemCount() {
        return recentActivities.size();
    }

    public static class RecentActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activitySummaryTextView;
        TextView activityDateTextView;
        TextView activityTimeTextView;
        TextView activityLocationTextView;

        public RecentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activitySummaryTextView = itemView.findViewById(R.id.activitySummaryTextView);
            activityDateTextView = itemView.findViewById(R.id.activityDateTextView);
            activityTimeTextView = itemView.findViewById(R.id.activityTimeTextView);
            activityLocationTextView = itemView.findViewById(R.id.activityLocationTextView);
        }
    }
}
