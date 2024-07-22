package com.example.primehomeservices;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;


public class Microservices extends AppCompatActivity implements MicroserviceAdapter.OnMicroserviceSelectedListener {

    private TextView serviceName;
    private TextView paymentSummary;
    private TextView itemTotalPrice;
    private TextView itemTotalDiscount;
    private TextView TotalServiceFee;
    private TextView FinalGrandTotal;
    private RecyclerView microservicesRecyclerView;
    private MicroserviceAdapter adapter;
    private List<MicroserviceClass> microservicesList;
    private List<MicroserviceClass> selectedMicroservices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microservices);

        serviceName = findViewById(R.id.service_name);
        itemTotalPrice = findViewById(R.id.itemTotalPrice);
        itemTotalDiscount = findViewById(R.id.itemTotalDiscount);
        TotalServiceFee = findViewById(R.id.TotalServiceFee);
        FinalGrandTotal = findViewById(R.id.FinalGrandTotal);
        microservicesRecyclerView = findViewById(R.id.microservices_recycler_view);
        microservicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the service name from the intent
        String serviceNameStr = getIntent().getStringExtra("serviceName");
        serviceName.setText(serviceNameStr);

        microservicesList = new ArrayList<>();
        selectedMicroservices = new ArrayList<>();
        adapter = new MicroserviceAdapter(this, microservicesList, this);
        microservicesRecyclerView.setAdapter(adapter);

        fetchMicroservices(serviceNameStr);
    }

    private void fetchMicroservices(String serviceNameStr) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        databaseReference.orderByChild("servicename").equalTo(serviceNameStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                microservicesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataSnapshot microservicesSnapshot = snapshot.child("microservices");
                    for (DataSnapshot microserviceSnapshot : microservicesSnapshot.getChildren()) {
                        Map<String, Object> microserviceData = (Map<String, Object>) microserviceSnapshot.getValue();
                        MicroserviceClass microservice = new MicroserviceClass(
                                (String) microserviceData.get("name"),
                                ((Long) microserviceData.get("price")).intValue(),
                                ((Long) microserviceData.get("discount")).intValue(),
                                ((Long) microserviceData.get("serviceFee")).intValue(),
                                ((Long) microserviceData.get("grandTotal")).intValue()
                        );
                        microservicesList.add(microservice);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Microservices.this, "Failed to load microservices", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMicroserviceSelected(MicroserviceClass microservice, boolean isSelected) {
        if (isSelected) {
            selectedMicroservices.add(microservice);
        } else {
            selectedMicroservices.remove(microservice);
        }
        updatePaymentSummary();
    }

    private void updatePaymentSummary() {
        int itemTotal = 0;
        int itemsDiscount = 0;
        int serviceFee = 0;

        for (MicroserviceClass microservice : selectedMicroservices) {
            itemTotal += microservice.getPrice();
            itemsDiscount += microservice.getDiscount();
            serviceFee += microservice.getServiceFee();
        }

        int grandTotal = itemTotal + serviceFee - itemsDiscount;

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        itemTotalPrice.setText(numberFormat.format(itemTotal));
        itemTotalDiscount.setText(numberFormat.format(itemsDiscount));
        TotalServiceFee.setText(numberFormat.format(serviceFee));
        FinalGrandTotal.setText(numberFormat.format(grandTotal));
    }
}
