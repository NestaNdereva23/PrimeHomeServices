package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primehomeservices.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    ActivitySignInBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        binding.SignInButton.setOnClickListener(v -> {
            String email = binding.loginEmail.getText().toString();
            String password = binding.loginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);
                    if (checkCredentials) {
                        Toast.makeText(SignInActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking credentials", e);
                    Toast.makeText(SignInActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.registerRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, registration.class);
            startActivity(intent);
        });

    }
}
