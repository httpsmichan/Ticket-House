package com.example.tickethouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

    private boolean shouldAllowBackPress = false;
    private LinearLayout movieListLayout;
    private LinearLayout comingSoonLayout;
    private DatabaseReference databaseReference;
    private ImageView logOutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logOutbtn = findViewById(R.id.logOutbtn);
        movieListLayout = findViewById(R.id.movieListLayout);
        comingSoonLayout = findViewById(R.id.comingSoonLayout);


        databaseReference = FirebaseDatabase.getInstance().getReference("movies");


        loadMoviesFromDatabase();

        logOutbtn.setOnClickListener(v -> {

            new AlertDialog.Builder(home.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        FirebaseAuth.getInstance().signOut();


                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();


                        Intent intent = new Intent(home.this, login.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadMoviesFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieListLayout.removeAllViews();

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


                movieImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, details.class);
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


                    startActivity(intent);

                });

                movieTitleView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, ticket.class);
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

                    startActivity(intent);
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


                comingSoonImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(home.this, details.class);
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

                    startActivity(intent);

                });

                comingSoonLayout.addView(comingSoonItemView);
            }
        } catch (Exception e) {
            Log.e("AddMovieError", "Error adding movie: " + e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBackPress) {

            super.onBackPressed();
        } else {

        }
    }
}