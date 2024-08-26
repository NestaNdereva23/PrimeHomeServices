package com.example.primehomeservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primehomeservices.mymodels.STKPushRequest;
import com.example.primehomeservices.services.DarajaApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.primehomeservices.Constants.BASE_URL;
import static com.example.primehomeservices.Constants.BUSINESS_SHORT_CODE;
import static com.example.primehomeservices.Constants.PARTYB;
//import static com.example.primehomeservices.Constants.PARTYA;
import static com.example.primehomeservices.Constants.PASSKEY;
import static com.example.primehomeservices.Constants.CALLBACKURL;
import static com.example.primehomeservices.Constants.TRANSACTION_TYPE;

import com.example.primehomeservices.mymodels.AccessToken;
import com.example.primehomeservices.mymodels.STKPushRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import timber.log.Timber;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentOptionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView payGrandTotal;
    private EditText paymentPhonenumber;
    private Button paynowBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;

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

        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setCancelable(false);

        paynowBtn.setOnClickListener(this);

        getAccessToken();

        String orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            fetchGrandTotal(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == paynowBtn) {
            String phone_number = paymentPhonenumber.getText().toString();
            String amount = payGrandTotal.getText().toString();
            performSTKPush(phone_number, amount);
        }
    }
    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    mApiClient.setAuthToken(response.body().accessToken);

                }else {
                    Timber.e("Failed to get access token: %s", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Timber.e(t, "Failed to get access token");
            }
        });
    }

    public void performSTKPush(String phone_number, String amount) {
        mProgressDialog.show();

        STKPushRequest stkPushRequest = new STKPushRequest(phone_number, amount);

        BackendService backendService = mApiClient.createService(BackendService.class);
        backendService.initiateStkPush(stkPushRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentOptionActivity.this, "Payment initiated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentOptionActivity.this, "Payment initiation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    private void fetchGrandTotal(String orderId) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            mDatabase.child("orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        OrdersClass order = snapshot.getValue(OrdersClass.class);
                        if (order != null && uid.equals(order.getUserId())) {
                            int grandTotal = order.getGrandTotal();
                            displayGrandTotal(grandTotal);
                        } else {
                            Toast.makeText(PaymentOptionActivity.this, "Order not found or access denied", Toast.LENGTH_SHORT).show();
                        }
                    } else {
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

    public interface BackendService{
        @POST("stkpush/")
        Call<Void> initiateStkPush(@Body STKPushRequest stkPushRequest);
    }

}
