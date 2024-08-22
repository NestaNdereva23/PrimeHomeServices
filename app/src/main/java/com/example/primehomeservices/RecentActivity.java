package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        // Schedule the WorkManager to run the CheckPendingOrdersWorker every hour
        CheckPendingOrdersWorker.scheduleWorker(this);

        recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView);
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recentActivities = new ArrayList<>();
        recentActivityAdapter = new RecentActivityAdapter(recentActivities);
        recentActivityRecyclerView.setAdapter(recentActivityAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchRecentActivities();
        fetchGrandTotal();

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
                }else if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchGrandTotal() {

        recentActivityAdapter.setOnItemClickListener(new RecentActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrdersClass ordersClass) {
                Intent intent = new Intent(RecentActivity.this, PaymentOptionActivity.class);
                intent.putExtra("grandTotal", ordersClass.getGrandTotal());
                intent.putExtra("orderId", ordersClass.getOrderId());
                startActivity(intent);
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
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                OrdersClass order = snapshot.getValue(OrdersClass.class);
                                if (order != null && "pending".equals(order.getStatus())) {
                                    order.setOrderId(snapshot.getKey());
                                    orders.add(order);

                                }
                            }
                            // Sort orders by timestamp in descending order
                            Collections.sort(orders, new Comparator<OrdersClass>() {
                                @Override
                                public int compare(OrdersClass o1, OrdersClass o2) {
                                    try {
                                        Date date1 = sdf.parse(o1.getTime());
                                        Date date2 = sdf.parse(o2.getTime());
                                        return date2.compareTo(date1); // Descending order
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
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
