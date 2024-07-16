package com.example.primehomeservices;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Home extends AppCompatActivity {

    RecyclerView recyclerView;
    HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference().child("Services").child("services");

        FirebaseRecyclerOptions<HomeModel> options =
                new FirebaseRecyclerOptions.Builder<HomeModel>()
                        .setQuery(query, HomeModel.class)
                        .build();

        homeAdapter = new HomeAdapter(options);
        recyclerView.setAdapter(homeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        homeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        homeAdapter.stopListening();
    }
}
