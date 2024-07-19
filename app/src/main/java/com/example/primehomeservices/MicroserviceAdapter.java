package com.example.primehomeservices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MicroserviceAdapter extends RecyclerView.Adapter<MicroserviceAdapter.ViewHolder> {
    private Context context;
    private List<MicroserviceClass> microservicesList;
    private OnMicroserviceSelectedListener listener;

    public MicroserviceAdapter(Context context, List<MicroserviceClass> microservicesList, OnMicroserviceSelectedListener listener) {
        this.context = context;
        this.microservicesList = microservicesList;
        this.listener = listener;
    }

    public MicroserviceAdapter(List<MicroserviceClass> microservicesList) {
        this.microservicesList = microservicesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_microservices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MicroserviceClass microservice = microservicesList.get(position);
        holder.microserviceName.setText(microservice.getName());
        holder.microservicePrice.setText(String.valueOf(microservice.getPrice()));
        holder.microserviceDiscount.setText(String.valueOf(microservice.getDiscount()));
        holder.microserviceServiceFee.setText(String.valueOf(microservice.getServiceFee()));
        holder.microserviceGrandTotal.setText(String.valueOf(microservice.getGrandTotal()));

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(microservice.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            microservice.setSelected(isChecked);
            listener.onMicroserviceSelected(microservice, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return microservicesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView microserviceName;
        TextView microservicePrice;
        TextView microserviceDiscount;
        TextView microserviceServiceFee;
        TextView microserviceGrandTotal;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            microserviceName = itemView.findViewById(R.id.microservice_name);
            microservicePrice = itemView.findViewById(R.id.microservice_price);
            microserviceDiscount = itemView.findViewById(R.id.microservice_discount);
            microserviceServiceFee = itemView.findViewById(R.id.microservice_service_fee);
            microserviceGrandTotal = itemView.findViewById(R.id.microservice_grand_total);
            checkBox = itemView.findViewById(R.id.microserviceCheckBox);
        }
    }

    public interface OnMicroserviceSelectedListener {
        void onMicroserviceSelected(MicroserviceClass microservice, boolean isSelected);
    }
}
