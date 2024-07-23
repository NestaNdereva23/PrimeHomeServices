package com.example.primehomeservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {
    private EditText username, firstname, lastname, phoneContact, location;
    private Button updateButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        username = findViewById(R.id.updateUsername);
        firstname = findViewById(R.id.updateFirstname);
        lastname = findViewById(R.id.updateLastname);
        phoneContact = findViewById(R.id.updateContact);
        location = findViewById(R.id.updateLocation);
        updateButton = findViewById(R.id.updateProfileBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        final String userUsername = username.getText().toString().trim();
        final String userFirstname = firstname.getText().toString().trim();
        final String userLastname = lastname.getText().toString().trim();
        final String userPhoneContact = phoneContact.getText().toString().trim();
        final String userLocation = location.getText().toString().trim();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();

            // Fetch the current email
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User existingUser = dataSnapshot.getValue(User.class);
                    if (existingUser != null) {
                        String email = existingUser.email; // Get the existing email

                        User updatedUser = new User(email, userUsername, userFirstname, userLastname, userPhoneContact, userLocation);
                        mDatabase.child("users").child(uid).setValue(updatedUser)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UpdateProfile.this, Home.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(UpdateProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(UpdateProfile.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateProfile.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }
}
