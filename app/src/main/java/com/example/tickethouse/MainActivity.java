package com.example.tickethouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStartedButton = findViewById(R.id.getStarted);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, firstpage.class);
                startActivity(intent);


                overridePendingTransition(0, 0);

                finish();
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
