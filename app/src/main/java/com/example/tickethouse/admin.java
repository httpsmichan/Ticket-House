package com.example.tickethouse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class admin extends AppCompatActivity {

    private EditText moviePhotoLink, movieTitle, moviePrice, movieDirector, movieDescription, movieCast, dateStart, dateEnd, startTime, endTime;
    private CheckBox movieGenre1, movieGenre2, movieGenre3, movieGenre4, movieGenre5, movieGenre6;
    private CheckBox selectTheatre1, selectTheatre2, selectTheatre3, selectTheatre4, selectTheatre5;
    private CheckBox movieLanguage1, movieLanguage2, movieLanguage3, movieLanguage4, movieLanguage5, movieLanguage6, movieLanguage7, movieLanguage8;
    private Button saveButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

        // Initialize EditText fields
        moviePhotoLink = findViewById(R.id.moviePhotolink);
        movieTitle = findViewById(R.id.movieTitle);
        moviePrice = findViewById(R.id.moviePrice);
        movieDirector = findViewById(R.id.movieDirector);
        movieDescription = findViewById(R.id.movieDescription);
        movieCast = findViewById(R.id.movieCast);
        dateStart = findViewById(R.id.datestart);
        dateEnd = findViewById(R.id.dateend);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        // Initialize CheckBoxes for genres
        movieGenre1 = findViewById(R.id.movieGenre1);
        movieGenre2 = findViewById(R.id.movieGenre2);
        movieGenre3 = findViewById(R.id.movieGenre3);
        movieGenre4 = findViewById(R.id.movieGenre4);
        movieGenre5 = findViewById(R.id.movieGenre5);
        movieGenre6 = findViewById(R.id.movieGenre6);

        // Initialize CheckBoxes for theaters
        selectTheatre1 = findViewById(R.id.selectTheatre1);
        selectTheatre2 = findViewById(R.id.selectTheatre2);
        selectTheatre3 = findViewById(R.id.selectTheatre3);
        selectTheatre4 = findViewById(R.id.selectTheatre4);
        selectTheatre5 = findViewById(R.id.selectTheatre5);

        // Initialize CheckBoxes for languages
        movieLanguage1 = findViewById(R.id.movieLanguage1);
        movieLanguage2 = findViewById(R.id.movieLanguage2);
        movieLanguage3 = findViewById(R.id.movieLanguage3);
        movieLanguage4 = findViewById(R.id.movieLanguage4);
        movieLanguage5 = findViewById(R.id.movieLanguage5);
        movieLanguage6 = findViewById(R.id.movieLanguage6);
        movieLanguage7 = findViewById(R.id.movieLanguage7);
        movieLanguage8 = findViewById(R.id.movieLanguage8);

        // Initialize save button
        saveButton = findViewById(R.id.saveButton);

        // Set save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovieDetails();
            }
        });
    }

    private void saveMovieDetails() {
        // Get values from EditText fields
        String photoLink = moviePhotoLink.getText().toString();
        String title = movieTitle.getText().toString();
        String price = moviePrice.getText().toString();
        String director = movieDirector.getText().toString();
        String description = movieDescription.getText().toString();
        String cast = movieCast.getText().toString();
        String startDate = dateStart.getText().toString();
        String endDate = dateEnd.getText().toString();
        String startTimeValue = startTime.getText().toString();
        String endTimeValue = endTime.getText().toString();

        // Get selected genres
        StringBuilder genre = new StringBuilder();
        if (movieGenre1.isChecked()) genre.append("Action, ");
        if (movieGenre2.isChecked()) genre.append("Comedy, ");
        if (movieGenre3.isChecked()) genre.append("Adventure, ");
        if (movieGenre4.isChecked()) genre.append("Horror, ");
        if (movieGenre5.isChecked()) genre.append("Romance, ");
        if (movieGenre6.isChecked()) genre.append("Sci-Fi, ");
        if (genre.length() > 0) genre.setLength(genre.length() - 2); // Remove last comma

        // Get selected theaters
        StringBuilder theaters = new StringBuilder();
        if (selectTheatre1.isChecked()) theaters.append("Theatre 1, ");
        if (selectTheatre2.isChecked()) theaters.append("Theatre 2, ");
        if (selectTheatre3.isChecked()) theaters.append("Theatre 3, ");
        if (selectTheatre4.isChecked()) theaters.append("VIP Room 1, ");
        if (selectTheatre5.isChecked()) theaters.append("VIP Room 2, ");
        if (theaters.length() > 0) theaters.setLength(theaters.length() - 2); // Remove last comma

        // Get selected languages
        StringBuilder languages = new StringBuilder();
        if (movieLanguage1.isChecked()) languages.append("English, ");
        if (movieLanguage2.isChecked()) languages.append("German, ");
        if (movieLanguage3.isChecked()) languages.append("Spanish, ");
        if (movieLanguage4.isChecked()) languages.append("Japanese, ");
        if (movieLanguage5.isChecked()) languages.append("Korean, ");
        if (movieLanguage6.isChecked()) languages.append("French, ");
        if (movieLanguage7.isChecked()) languages.append("Mandarin Chinese, ");
        if (movieLanguage8.isChecked()) languages.append("Hindi, ");
        if (languages.length() > 0) languages.setLength(languages.length() - 2); // Remove last comma

        // Validate input
        if (photoLink.isEmpty() || title.isEmpty() || price.isEmpty() || director.isEmpty() || description.isEmpty()
                || cast.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || startTimeValue.isEmpty()
                || endTimeValue.isEmpty() || genre.length() == 0 || theaters.length() == 0 || languages.length() == 0) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for the movie
        String movieId = databaseReference.push().getKey();

        // Create a movie object
        moviedata movie = new moviedata(photoLink, title, price, director, description, cast, startDate, endDate, startTimeValue, endTimeValue, genre.toString(), theaters.toString(), languages.toString());

        // Save the movie object to Firebase
        if (movieId != null) {
            databaseReference.child(movieId).setValue(movie)
                    .addOnSuccessListener(aVoid -> Toast.makeText(admin.this, "Movie details saved successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(admin.this, "Failed to save movie details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    Log.d("MovieDatabase", "data added");
        }
    }
}
