package com.example.primehomeservices;// MicroserviceActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.primehomeservices.MicroserviceAdapter;
import com.example.primehomeservices.MicroserviceClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Microservices extends AppCompatActivity {

    private TextView serviceName;
    private RecyclerView microservicesRecyclerView;
    private MicroserviceAdapter adapter;
    private List<MicroserviceClass> microservicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microservices);

        serviceName = findViewById(R.id.service_name);
        microservicesRecyclerView = findViewById(R.id.microservices_recycler_view);
        microservicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the service name from the intent
        Intent intent = getIntent();
        String serviceNameStr = intent.getStringExtra("serviceName");
        serviceName.setText(serviceNameStr);

        microservicesList = new ArrayList<>();
        adapter = new MicroserviceAdapter(microservicesList);
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @NonNull
    private static MicroserviceClass getMicroserviceClass(Map.Entry<String, Object> entry) {
        Map<String, Object> microserviceData = (Map<String, Object>) entry.getValue();
        MicroserviceClass microservice = new MicroserviceClass(
                (String) microserviceData.get("name"),
                ((Long) microserviceData.get("price")).intValue(),
                ((Long) microserviceData.get("discount")).intValue(),
                ((Long) microserviceData.get("serviceFee")).intValue(),
                ((Long) microserviceData.get("grandTotal")).intValue()
        );
        return microservice;
    }
}
