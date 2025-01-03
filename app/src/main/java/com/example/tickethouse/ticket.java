package com.example.tickethouse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView movieTitleTextView;
    private TextView movieGenreTextView;
    private TextView moviePriceTextView;  // TextView for movie price
    private TextView ticketDateTextView;  // TextView for reservation date
    private TextView ticketSeatTextView;  // TextView for reserved seats
    private TextView ticketCodeTextView;  // TextView for ticket code (5-digit code)
    private ImageView posterImageView;
    private ImageView barcodeImageView; // ImageView for barcode
    private DatabaseReference moviesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);  // Set the layout for ticket activity

        // Initialize views
        movieTitleTextView = findViewById(R.id.tickettitle);  // TextView for movie title
        movieGenreTextView = findViewById(R.id.ticketgenre);  // TextView for movie genre
        moviePriceTextView = findViewById(R.id.ticketpricetext);  // TextView for movie price
        ticketDateTextView = findViewById(R.id.ticketdatetext);  // TextView for reservation date
        ticketSeatTextView = findViewById(R.id.ticketseattext);  // TextView for reserved seats
        ticketCodeTextView = findViewById(R.id.ticketcodenum);  // TextView for displaying the ticket code
        posterImageView = findViewById(R.id.posterViewTop);  // ImageView for movie poster
        barcodeImageView = findViewById(R.id.ticketcode); // ImageView for barcode

        // Initialize Firebase reference to movies collection
        moviesRef = FirebaseDatabase.getInstance().getReference("movies");

        // Get the movie title and reservation details passed through the intent
        String movieTitle = getIntent().getStringExtra("movieTitle");
        String reservationDate = getIntent().getStringExtra("reservationDate");
        String seats = getIntent().getStringExtra("seats");

        // Set the movie title and reservation details in the TextViews
        movieTitleTextView.setText(movieTitle);
        ticketDateTextView.setText(reservationDate);

        // Format the seats (if multiple, separate by commas)
        if (seats != null && !seats.isEmpty()) {
            ticketSeatTextView.setText(seats);  // Directly show seats if available
        } else {
            ticketSeatTextView.setText("Seats: None selected");
        }

        // Fetch the movie details from the Firebase database based on the title
        fetchMovieDetails(movieTitle);

        // Generate a random 5-digit code for the ticket
        String randomCode = generateRandomCode();

        // Display the generated ticket code on the TextView
        ticketCodeTextView.setText(randomCode);

        // Generate the barcode for the 5-digit code
        generateBarcode(randomCode);

        // Optionally, you can display the code on the screen for clarity
        Toast.makeText(this, "Generated Code: " + randomCode, Toast.LENGTH_LONG).show();
    }

    // Method to generate a random 5-digit code
    private String generateRandomCode() {
        int code = (int) (Math.random() * 90000) + 10000;  // Generate a 5-digit number (10000 to 99999)
        return String.valueOf(code);
    }

    // Method to generate and display barcode from the 5-digit code
    private void generateBarcode(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Generate barcode using the 5-digit code
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, 600, 300);
            barcodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating barcode.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMovieDetails(String movieTitle) {
        // Query the database for the movie based on the title
        moviesRef.orderByChild("title").equalTo(movieTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the movie data from the snapshot
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the movie data
                        String movieImage = snapshot.child("photoLink").getValue(String.class);
                        String movieGenre = snapshot.child("genre").getValue(String.class);  // Get genre
                        String moviePrice = snapshot.child("price").getValue(String.class);  // Get price

                        // Set the genre and price to the corresponding TextViews
                        if (movieGenre != null) {
                            movieGenreTextView.setText("Genre: " + movieGenre);
                        }
                        if (moviePrice != null) {
                            moviePriceTextView.setText("Price: " + moviePrice);
                        }

                        // Check if movieImage is not null or empty
                        if (movieImage != null && !movieImage.isEmpty()) {
                            // Load the poster image using Glide
                            Glide.with(ticket.this)
                                    .load(movieImage)  // Load the image from the URL
                                    .into(posterImageView);  // Set the loaded image into the ImageView
                        } else {
                            // Handle case where the image URL is not available
                            Toast.makeText(ticket.this, "Image not available for this movie.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // If movie not found, show a toast
                    Toast.makeText(ticket.this, "Movie not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ticket.this, "Error retrieving movie details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
