package com.example.tickethouse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class home extends AppCompatActivity {

    private LinearLayout movieListLayout;
    private LinearLayout comingSoonLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        movieListLayout = findViewById(R.id.movieListLayout); // LinearLayout to display movies
        comingSoonLayout = findViewById(R.id.comingSoonLayout);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

        // Load movies from the database
        loadMoviesFromDatabase();
    }

    private void loadMoviesFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieListLayout.removeAllViews(); // Clear previous views

                if (dataSnapshot.exists()) {
                    for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                        moviedata movie = movieSnapshot.getValue(moviedata.class);
                        if (movie != null) {
                            addMovieToLayout(movie);
                        }
                    }
                } else {
                    Toast.makeText(home.this, "No movies available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(home.this, "Failed to load movies: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void addMovieToLayout(moviedata movie) {
        try {
            Log.d("MovieData", "Processing movie: " + movie.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date startDate = sdf.parse(movie.getStartDate());
            Date currentDate = new Date();
            long diff = startDate.getTime() - currentDate.getTime();
            long daysUntilStart = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (daysUntilStart <= 15) {
                Log.d("Layout", "Adding to now showing: " + movie.getTitle());
                View movieItemView = getLayoutInflater().inflate(R.layout.movie_item, movieListLayout, false);
                ImageView movieImageView = movieItemView.findViewById(R.id.movieImageView);
                TextView movieTitleView = movieItemView.findViewById(R.id.movieTitleView);

                Glide.with(this)
                        .load(movie.getPhotoLink())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(movieImageView);

                movieTitleView.setText(movie.getTitle());

                // Set an OnClickListener to navigate to the details page
                movieImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, details.class);
                    intent.putExtra("movieTitle", movie.getTitle()); // Passing movie data (e.g. title)
                    intent.putExtra("movieImage", movie.getPhotoLink()); // Passing movie image URL
                    intent.putExtra("moviePrice", movie.getPrice());
                    intent.putExtra("movieCasts", movie.getCast());
                    intent.putExtra("movieDescription", movie.getDescription());
                    intent.putExtra("movieGenre", movie.getGenre());
                    intent.putExtra("movieDirector", movie.getDirector());
                    intent.putExtra("movieLanguage", movie.getLanguages());
                    intent.putExtra("movieStartTime", movie.getStartTime());
                    intent.putExtra("movieEndTime", movie.getEndTime());
                    intent.putExtra("movieStartDate", movie.getStartDate());
                    intent.putExtra("movieEndDate", movie.getEndDate());
                    intent.putExtra("movieTheatres", movie.getTheaters());


                    startActivity(intent);

                });

                movieTitleView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, ticket.class); // Navigate to ticket activity
                    intent.putExtra("movieTitle", movie.getTitle());
                    intent.putExtra("movieImage", movie.getPhotoLink());
                    intent.putExtra("moviePrice", movie.getPrice());
                    intent.putExtra("movieCasts", movie.getCast());
                    intent.putExtra("movieDescription", movie.getDescription());
                    intent.putExtra("movieGenre", movie.getGenre());
                    intent.putExtra("movieDirector", movie.getDirector());
                    intent.putExtra("movieLanguage", movie.getLanguages());
                    intent.putExtra("movieStartTime", movie.getStartTime());
                    intent.putExtra("movieEndTime", movie.getEndTime());
                    intent.putExtra("movieStartDate", movie.getStartDate());
                    intent.putExtra("movieEndDate", movie.getEndDate());
                    intent.putExtra("movieTheatres", movie.getTheaters());

                    startActivity(intent); // Navigate to the ticket page
                });

                movieListLayout.addView(movieItemView);
            } else {
                Log.d("Layout", "Adding to coming soon: " + movie.getTitle());
                View comingSoonItemView = getLayoutInflater().inflate(R.layout.coming_soon, comingSoonLayout, false);
                ImageView comingSoonImageView = comingSoonItemView.findViewById(R.id.comingSoonView);
                TextView comingSoonTitleView = comingSoonItemView.findViewById(R.id.comingsoonTitleView);

                Glide.with(this)
                        .load(movie.getPhotoLink())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(comingSoonImageView);

                comingSoonTitleView.setText(movie.getTitle());

                // Set an OnClickListener for the coming soon movies as well
                comingSoonImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, details.class);
                    intent.putExtra("movieTitle", movie.getTitle()); // Passing movie data
                    intent.putExtra("movieImage", movie.getPhotoLink());
                    intent.putExtra("moviePrice", movie.getPrice());
                    intent.putExtra("movieCasts", movie.getCast());
                    intent.putExtra("movieDescription", movie.getDescription());
                    intent.putExtra("movieGenre", movie.getGenre());
                    intent.putExtra("movieDirector", movie.getDirector());
                    intent.putExtra("movieLanguage", movie.getLanguages());
                    intent.putExtra("movieStartTime", movie.getStartTime());
                    intent.putExtra("movieEndTime", movie.getEndTime());
                    intent.putExtra("movieStartDate", movie.getStartDate());
                    intent.putExtra("movieEndDate", movie.getEndDate());
                    intent.putExtra("movieTheatres", movie.getTheaters());

                    startActivity(intent);

                });

                comingSoonLayout.addView(comingSoonItemView);
            }
        } catch (Exception e) {
            Log.e("AddMovieError", "Error adding movie: " + e.getMessage(), e);
        }
    }
}