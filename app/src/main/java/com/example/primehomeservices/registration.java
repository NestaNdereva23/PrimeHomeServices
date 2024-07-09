package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primehomeservices.databinding.ActivityRegistrationBinding;

public class registration extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.registerEmail.getText().toString();
                String password = binding.registerPassword.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();

                if (email.equals("") || password.equals("") || confirmPassword.equals(""))
                    Toast.makeText(registration.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                else {
                    if  (password.equals(confirmPassword)){
                        Boolean checkUserEmail = databaseHelper.checkEmail((email));

                        if (checkUserEmail == false){
                            Boolean insert = databaseHelper.insertData(email, password);

                            if (insert == true){
                                Toast.makeText(registration.this, "Registration success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(registration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(registration.this, "User already exists, Please login", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(registration.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);

            }
        });

    }
}