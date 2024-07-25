package com.example.primehomeservices;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {
    private TextView itemTotalPrice;
    private TextView itemTotalDiscount;
    private TextView TotalServiceFee;
    private TextView FinalGrandTotal;
    private Button proceedtoPaymentBtn;
    private EditText locationEditText;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button dateButton;
    private Button timeButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);

        itemTotalPrice = findViewById(R.id.itemTotalPrice);
        itemTotalDiscount = findViewById(R.id.itemTotalDiscount);
        TotalServiceFee = findViewById(R.id.TotalServiceFee);
        FinalGrandTotal = findViewById(R.id.FinalGrandTotal);
        locationEditText = findViewById(R.id.locationEditText);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        proceedtoPaymentBtn = findViewById(R.id.proceedtoPaymentBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_summary);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    return true;
                } else if (itemId == R.id.navigation_services) {
                    // Start the Service activity
                    startActivity(new Intent(getApplicationContext(), RecentActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_summary) {
                    startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_home) {
                    // Start the Profile activity
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    return true;
                }
                return false;
            }
        });

        //fetch and display default location
        fetchDefaultLocation();
        displayPaymentSummary();

        // Set up date and time pickers
        setupDatePicker();
        setupTimePicker();

        proceedtoPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderToDatabase();
            }
        });
    }
    private  void fetchDefaultLocation() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            String uid = firebaseUser.getUid();

            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null){
                        locationEditText.setText(user.location);
                    }else{
                        Toast.makeText(SummaryActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SummaryActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }
    private void displayPaymentSummary(){
        Intent intent = getIntent();
        int ItemTotal = intent.getIntExtra("itemTotal", 0);
        int ItemsDiscount = intent.getIntExtra("itemsDiscount", 0);
        int ServiceFee = intent.getIntExtra("serviceFee", 0);
        int grandTotal = intent.getIntExtra("grandTotal", 0);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        itemTotalPrice.setText(numberFormat.format(ItemTotal));
        itemTotalDiscount.setText(numberFormat.format(ItemsDiscount));
        TotalServiceFee.setText(numberFormat.format(ServiceFee));
        FinalGrandTotal.setText(numberFormat.format(grandTotal));
    }
    private void setupDatePicker() {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SummaryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }
    private void setupTimePicker() {
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SummaryActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeTextView.setText(hourOfDay + ":" + String.format("%02d", minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }
    private void saveOrderToDatabase()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            String location = locationEditText.getText().toString();
            String date = dateTextView.getText().toString();
            String time = timeTextView.getText().toString();

            Intent intent = getIntent();
            int itemTotal = intent.getIntExtra("itemTotal", 0);
            int itemsDiscount = intent.getIntExtra("itemsDiscount", 0);
            int serviceFee = intent.getIntExtra("serviceFee", 0);
            int grandTotal = intent.getIntExtra("grandTotal", 0);

            OrdersClass order = new OrdersClass(userId, itemTotal, itemsDiscount, serviceFee, grandTotal, "pending", location, date, time);

            String orderId = mDatabase.child("orders").push().getKey();
            mDatabase.child("orders").child(orderId).setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SummaryActivity.this, "Order saved successfully", Toast.LENGTH_SHORT).show();
                            proceedToPayment(orderId);
                        } else {
                            Toast.makeText(SummaryActivity.this, "Failed to save order", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceedToPayment(String orderId) {
        Intent intent = new Intent(SummaryActivity.this, PaymentOptionActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

//    private void passtoRecent(String orderId){
//        Intent intent = new Intent(SummaryActivity.this, RecentActivity.class);
//        intent.putExtra("orderId", orderId);
//        startActivity(intent);
//    }
}



