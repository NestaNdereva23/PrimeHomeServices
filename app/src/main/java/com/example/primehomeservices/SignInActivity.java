package com.example.primehomeservices;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private TextView registerRedirectText;
    private Button SignInButton;
    private FirebaseAuth mAuth;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sessionManager = new SessionManager(getApplicationContext());

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        SignInButton = findViewById(R.id.SignInButton);
        registerRedirectText = findViewById(R.id.registerRedirectText);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        registerRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, registration.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNetworkAvailable()) {
            // Online Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                sessionManager.createLoginSession(userId);

                                // Store user session data using SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", userId); // Store the user ID or token
                                editor.putBoolean("isLoggedIn", true); // Mark the user as logged in
                                editor.apply();

                                Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignInActivity.this, Home.class));
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Offline Authentication using SQLite
            if (authenticateOffline(email, password)) {
                // Store user session data using SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", email); //
                editor.putBoolean("isLoggedIn", true); // Mark the user as logged in
                editor.apply();

                Toast.makeText(SignInActivity.this, "Offline Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, Home.class));
                finish();
            } else {
                Toast.makeText(SignInActivity.this, "Offline Login failed: Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean authenticateOffline(String email, String password) {
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_PASSWORD},
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD));
            cursor.close();
            return storedPassword.equals(password);
        }

        if (cursor != null) {
            cursor.close();
        }

        return false;
    }
}
