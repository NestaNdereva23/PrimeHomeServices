package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentActivity extends AppCompatActivity {
    private RecyclerView recentActivityRecyclerView;
    private RecentActivityAdapter recentActivityAdapter;
    private List<OrdersClass> recentActivities;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView);
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recentActivities = new ArrayList<>();
        recentActivityAdapter = new RecentActivityAdapter(recentActivities);
        recentActivityRecyclerView.setAdapter(recentActivityAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchRecentActivities();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_services);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_services) {
                    startActivity(new Intent(getApplicationContext(), RecentActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
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

    private void fetchRecentActivities() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            mDatabase.child("orders").orderByChild("userId").equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<OrdersClass> orders = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                OrdersClass order = snapshot.getValue(OrdersClass.class);
                                if (order != null && "pending".equals(order.getStatus())) {
                                    orders.add(order);
                                }
                            }
                            // Sort orders by timestamp in descending order
                            Collections.sort(orders, new Comparator<OrdersClass>() {
                                @Override
                                public int compare(OrdersClass o1, OrdersClass o2) {
                                    return String.CASE_INSENSITIVE_ORDER.compare(o2.getTime(), o1.getTime());
                                }
                            });
                            updateUI(orders);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RecentActivity.this, "Failed to fetch recent activities", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(List<OrdersClass> orders) {
        recentActivities.clear();
        recentActivities.addAll(orders);
        recentActivityAdapter.notifyDataSetChanged();
    }
}
