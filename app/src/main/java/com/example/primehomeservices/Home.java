package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    GridView gridView;
    ArrayList<DataClass> dataList;
    HomeAdapter homeAdapter;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        homeAdapter = new HomeAdapter(dataList, this);
        gridView.setAdapter(homeAdapter);

        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataClass dataClass) {
                Intent intent = new Intent(Home.this, Microservices.class);
                intent.putExtra("serviceName", dataClass.getServicename());
                startActivity(intent);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    return true;
                } else if (itemId == R.id.navigation_services) {
                    startActivity(new Intent(getApplicationContext(), RecentActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_summary) {
                    startActivity(new Intent(getApplicationContext(), PaymentOptionActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    return true;
                }
                return false;
            }
        });
    }
}
