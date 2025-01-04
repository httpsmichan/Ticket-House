package com.example.tickethouse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ticket extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private TextView movieTitleTextView;
    private TextView movieGenreTextView;
    private TextView moviePriceTextView;
    private TextView ticketDateTextView;
    private TextView ticketSeatTextView;
    private TextView ticketCodeTextView;
    private ImageView posterImageView;
    private ImageView barcodeImageView;
    private DatabaseReference moviesRef;

    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);


        movieTitleTextView = findViewById(R.id.tickettitle);
        movieGenreTextView = findViewById(R.id.ticketgenre);
        moviePriceTextView = findViewById(R.id.ticketpricetext);
        ticketDateTextView = findViewById(R.id.ticketdatetext);
        ticketSeatTextView = findViewById(R.id.ticketseattext);
        ticketCodeTextView = findViewById(R.id.ticketcodenum);
        posterImageView = findViewById(R.id.posterViewTop);
        barcodeImageView = findViewById(R.id.ticketcode);


        moviesRef = FirebaseDatabase.getInstance().getReference("movies");


        homeButton = findViewById(R.id.homeButton);


        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ticket.this, home.class);
            startActivity(intent);
            finish();
        });

        String movieTitle = getIntent().getStringExtra("movieTitle");
        String reservationDate = getIntent().getStringExtra("reservationDate");
        String seats = getIntent().getStringExtra("seats");


        movieTitleTextView.setText(movieTitle);
        ticketDateTextView.setText(reservationDate);


        if (seats != null && !seats.isEmpty()) {
            ticketSeatTextView.setText(seats);
        } else {
            ticketSeatTextView.setText("Seats: None selected");
        }


        fetchMovieDetails(movieTitle);


        String randomCode = generateRandomCode();


        ticketCodeTextView.setText(randomCode);


        generateBarcode(randomCode);


        Toast.makeText(this, "Generated Code: " + randomCode, Toast.LENGTH_LONG).show();
    }

    private String generateRandomCode() {
        int code = (int) (Math.random() * 90000) + 10000;
        return String.valueOf(code);
    }


    private void generateBarcode(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {

            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, 600, 300);
            barcodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating barcode.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMovieDetails(String movieTitle) {
        moviesRef.orderByChild("title").equalTo(movieTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the movie data from the snapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the movie data
                        String movieImage = snapshot.child("photoLink").getValue(String.class);
                        String movieGenre = snapshot.child("genre").getValue(String.class);
                        String moviePrice = snapshot.child("price").getValue(String.class);


                        if (movieGenre != null) {
                            movieGenreTextView.setText("Genre: " + movieGenre);
                        }
                        if (moviePrice != null) {
                            moviePriceTextView.setText("Price: " + moviePrice);
                        }


                        if (movieImage != null && !movieImage.isEmpty()) {

                            Glide.with(ticket.this)
                                    .load(movieImage)
                                    .into(posterImageView);
                        } else {

                            Toast.makeText(ticket.this, "Image not available for this movie.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    Toast.makeText(ticket.this, "Movie not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ticket.this, "Error retrieving movie details.", Toast.LENGTH_SHORT).show();
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
