package com.example.primehomeservices;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import com.example.primehomeservices.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UpdateProfile extends AppCompatActivity {
    private EditText username, firstname, lastname, phoneContact, location;
    private Button updateButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int CAMERA_PERMISSION_CODE = 1;

    ActivityUpdateProfileBinding updateProfileBinding;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateProfileBinding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(updateProfileBinding.getRoot());

        username = findViewById(R.id.updateUsername);
        firstname = findViewById(R.id.updateFirstname);
        lastname = findViewById(R.id.updateLastname);
        phoneContact = findViewById(R.id.updateContact);
        location = findViewById(R.id.updateLocation);
        updateButton = findViewById(R.id.updateProfileBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageUri = createUri();
        registerPictureLauncher();

        updateProfileBinding.btnTakePicture.setOnClickListener(view -> {
            checkCameraPermissionAndOpenCamera();

        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageAndSaveProfile();
            }
        });
    }

    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.primehomeservices.fileProvider",
                imageFile
        );
    }

    private void registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try {
                            if (result) {
                                updateProfileBinding.profilePhoto.setImageURI(null);
                                updateProfileBinding.profilePhoto.setImageURI(imageUri);
                            }
                        } catch (Exception exception) {
                            exception.getStackTrace();
                        }
                    }
                }
        );
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(UpdateProfile.this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateProfile.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            takePictureLauncher.launch(imageUri);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(imageUri);
            } else {
                Toast.makeText(this, "Camera permission denied, Allow permission to take picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageAndSaveProfile() {
        if (imageUri != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            final StorageReference imageRef = storageReference.child("profile_images/" + uid + ".jpg");

            // Upload image
            imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    saveUserProfile(downloadUri.toString());
                                } else {
                                    Toast.makeText(UpdateProfile.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UpdateProfile.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // No image selected, just save the profile
            saveUserProfile(null);
        }
    }

    private void saveUserProfile(String imageUrl) {
        final String userUsername = username.getText().toString().trim();
        final String userFirstname = firstname.getText().toString().trim();
        final String userLastname = lastname.getText().toString().trim();
        final String userPhoneContact = phoneContact.getText().toString().trim();
        final String userLocation = location.getText().toString().trim();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();

            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User existingUser = dataSnapshot.getValue(User.class);
                    if (existingUser != null) {
                        String email = existingUser.email; // Get the existing email

                        // Update user details and image URL
                        User updatedUser = new User(email, userUsername, userFirstname, userLastname, userPhoneContact, userLocation, imageUrl );
                        if (imageUrl != null) {
                            updatedUser.setProfileImageUrl(imageUrl);
                        }

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