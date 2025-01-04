package com.example.tickethouse;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class firstpage extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private Button mainLgnButton, mainSgnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);


        mainLgnButton = findViewById(R.id.mainlgnbutton);
        mainSgnButton = findViewById(R.id.mainsgnbutton);


        mainLgnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(firstpage.this, login.class);
                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        });

        mainSgnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(firstpage.this, signup.class);
                startActivity(intent);

                overridePendingTransition(0, 0);
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