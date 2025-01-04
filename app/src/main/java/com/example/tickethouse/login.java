package com.example.tickethouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.loginusername);
        passwordEditText = findViewById(R.id.loginpassword);
        Button loginButton = findViewById(R.id.loginbutton);
        TextView registerRedirect = findViewById(R.id.redirectregister);

        loginButton.setOnClickListener(v -> loginUser());

        registerRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if (email.equals("ticketshouseadmin10@gmail.com") && password.equals("ticketshouse590")) {
                            intent = new Intent(login.this, adminhome.class);
                            overridePendingTransition(0, 0);
                        } else {
                            intent = new Intent(login.this, home.class);
                            overridePendingTransition(0, 0);
                        }

                        startActivity(intent);

                        overridePendingTransition(0, 0);

                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(login.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
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
