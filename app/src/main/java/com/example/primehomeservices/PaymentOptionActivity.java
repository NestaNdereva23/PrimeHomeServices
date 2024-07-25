package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;


public class PaymentOptionActivity extends AppCompatActivity {
    private TextView payGrandTotal;
    private EditText paymentPhonenumber;
    private Button paynowBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_option);

        payGrandTotal = findViewById(R.id.payGrandTotal);
        paymentPhonenumber = findViewById(R.id.paymentPhonenumber);
        paynowBtn = findViewById(R.id.paynowBtn);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            fetchGrandTotal(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchGrandTotal(String orderId) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            mDatabase.child("orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        OrdersClass order = snapshot.getValue(OrdersClass.class);
                        if (order != null && uid.equals(order.getUserId())){
                            int grandTotal = order.getGrandTotal();
                            displayGrandTotal(grandTotal);
                    }else {
                        Toast.makeText(PaymentOptionActivity.this, "Order not found or access denied", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(PaymentOptionActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PaymentOptionActivity.this, "Failed to fetch order", Toast.LENGTH_SHORT).show();
                }
            });

        }
}

private void displayGrandTotal(int grandTotal) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    payGrandTotal.setText(numberFormat.format(grandTotal));
}
}