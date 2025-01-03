package com.example.tickethouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

public class details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Find the views
        TextView movieTitleView = findViewById(R.id.filmtitle);
        ImageView movieImageView = findViewById(R.id.imageViewTop);
        TextView moviePriceView = findViewById(R.id.displayprice);
        TextView movieDescriptionView = findViewById(R.id.displaydescription);
        TextView movieCastsView = findViewById(R.id.displaycasts);
        TextView movieGenreView = findViewById(R.id.displaygenre);
        TextView movieDirectorView = findViewById(R.id.displaydirector);
        TextView movieLanguageView = findViewById(R.id.displaylanguage);
        TextView movieTimeView = findViewById(R.id.displaytime);
        Button setupMovieButton = findViewById(R.id.setupmoviebtn);

        // Get the passed data from the intent
        String movieTitle = getIntent().getStringExtra("movieTitle");
        String movieImage = getIntent().getStringExtra("movieImage");
        String moviePrice = getIntent().getStringExtra("moviePrice");
        String movieCasts = getIntent().getStringExtra("movieCasts");
        String movieDescription = getIntent().getStringExtra("movieDescription");
        String movieGenre = getIntent().getStringExtra("movieGenre");
        String movieDirector = getIntent().getStringExtra("movieDirector");
        String movieLanguage = getIntent().getStringExtra("movieLanguage");
        String movieStartTime = getIntent().getStringExtra("movieStartTime");
        String movieEndTime = getIntent().getStringExtra("movieEndTime");
        String movieStartDate = getIntent().getStringExtra("movieStartDate");
        String movieEndDate = getIntent().getStringExtra("movieEndDate");
        String movieTheatres = getIntent().getStringExtra("movieTheatres");

        // Set the data to the views
        if (movieTitle != null) {
            movieTitleView.setText(movieTitle);
        }

        if (movieImage != null) {
            Glide.with(this)
                    .load(movieImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(movieImageView);
        }

        if (moviePrice != null) {
            moviePriceView.setText(moviePrice);
            movieDescriptionView.setText(movieDescription);
            movieCastsView.setText(movieCasts);
            movieGenreView.setText(movieGenre);
            movieDirectorView.setText("Directed By: " + movieDirector);
            movieLanguageView.setText(movieLanguage);
            movieTimeView.setText(movieStartTime + " - " + movieEndTime);
        }

        // Set up the button click listener for setup
        setupMovieButton.setOnClickListener(v -> {
            // Create Intent for Reservation Activity
            Intent reservationIntent = new Intent(details.this, reservation.class);

            // Pass the movie details to the Reservation Activity
            reservationIntent.putExtra("movieTitle", movieTitle);
            reservationIntent.putExtra("moviePrice", moviePrice);
            reservationIntent.putExtra("movieDescription", movieDescription);
            reservationIntent.putExtra("movieStartDate", movieStartDate);
            reservationIntent.putExtra("movieEndDate", movieEndDate);
            reservationIntent.putExtra("movieTheatres", movieTheatres);
            reservationIntent.putExtra("movieStartTime", movieStartTime);
            reservationIntent.putExtra("movieEndTime", movieEndTime);

            // Start the Reservation Activity
            startActivity(reservationIntent);
        });
    }
}
