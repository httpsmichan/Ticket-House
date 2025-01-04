package com.example.tickethouse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class signup extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private static final String TAG = "register";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginRedirectTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeFirebase();
        initializeUIComponents();


        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception: ", throwable);
        });

        Log.d(TAG, "Firebase initialized successfully");

        loginRedirectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
    private void initializeFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initializeUIComponents() {
        usernameEditText = findViewById(R.id.registerusername);
        emailEditText = findViewById(R.id.registeremail);
        passwordEditText = findViewById(R.id.registerpassword);
        confirmPasswordEditText = findViewById(R.id.registercpassword);
        registerButton = findViewById(R.id.registerbutton);
        loginRedirectTextView = findViewById(R.id.redirectlogin);

        registerButton.setOnClickListener(v -> validateAndRegisterUser());
        loginRedirectTextView.setOnClickListener(v -> finish());
    }

    private void validateAndRegisterUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();
            registerUser(email, password, username);
        }
    }

    private void registerUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), username, email);
                        }
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username, String email) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);

        db.collection("datas").document(userId).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data saved to Firestore");
                    Toast.makeText(this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user data to Firestore", e);
                    Toast.makeText(this, "Failed to save user data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBackPress) {

            super.onBackPressed();
        } else {

        }
    }
}