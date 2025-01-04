package com.example.tickethouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class adminhome extends AppCompatActivity {

    private boolean shouldAllowBackPress = false;
    private LinearLayout adminmovieListLayout;
    private LinearLayout reservationsListLayout;
    private DatabaseReference moviesReference;
    private DatabaseReference reservationsReference;
    private Button addMovieBtn;
    private ImageView logOutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminhome);

        adminmovieListLayout = findViewById(R.id.adminmovieListLayout);
        reservationsListLayout = findViewById(R.id.reservationsListLayout);


        moviesReference = FirebaseDatabase.getInstance().getReference("movies");
        reservationsReference = FirebaseDatabase.getInstance().getReference("reservations");

        addMovieBtn = findViewById(R.id.addMoviebtn);
        logOutbtn = findViewById(R.id.logOutbtn);


        addMovieBtn.setOnClickListener(v -> {
            Intent intent = new Intent(adminhome.this, admin.class);
            startActivity(intent);
        });

        logOutbtn.setOnClickListener(v -> {

            new AlertDialog.Builder(adminhome.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        FirebaseAuth.getInstance().signOut();


                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();


                        Intent intent = new Intent(adminhome.this, login.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        loadMovies();
        loadReservations();
    }

    private void loadMovies() {
        moviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adminmovieListLayout.removeAllViews();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                        moviedata movie = movieSnapshot.getValue(moviedata.class);
                        if (movie != null) {
                            addMovieToAdminLayout(movie, movieSnapshot.getKey());
                        }
                    }
                } else {
                    Toast.makeText(adminhome.this, "No movies available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(adminhome.this, "Failed to load movies: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void loadReservations() {
        reservationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservationsListLayout.removeAllViews();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                        reservationdata reservation = reservationSnapshot.getValue(reservationdata.class);
                        if (reservation != null) {
                            addReservationToLayout(reservation, reservationSnapshot.getKey());
                        }
                    }
                } else {
                    Toast.makeText(adminhome.this, "No reservations available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(adminhome.this, "Failed to load reservations: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void updateMovieDetail(String key, String field, String value) {

        DatabaseReference movieRef = moviesReference.child(key);
        movieRef.child(field).setValue(value).addOnSuccessListener(aVoid -> {
            Toast.makeText(adminhome.this, "Movie " + field + " updated successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(adminhome.this, "Failed to update " + field, Toast.LENGTH_SHORT).show();
        });
    }


    private void addMovieToAdminLayout(moviedata movie, String key) {
        View movieItemView = getLayoutInflater().inflate(R.layout.admin_item, adminmovieListLayout, false);
        TextView titleView = movieItemView.findViewById(R.id.TitleView);
        TextView genreView = movieItemView.findViewById(R.id.GenreView);
        ImageView editView = movieItemView.findViewById(R.id.editimageView);
        ImageView deleteView = movieItemView.findViewById(R.id.deleteimageView);

        titleView.setText(movie.getTitle());
        genreView.setText(movie.getGenre());

        editView.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(adminhome.this);


            View modalView = getLayoutInflater().inflate(R.layout.update_modal, null);


            builder.setView(modalView);


            AlertDialog dialog = builder.create();


            ArrayList<String> updateOptions = new ArrayList<>();
            updateOptions.add("Choose Details to Update");
            updateOptions.add("Movie Poster Link");
            updateOptions.add("Movie Title");
            updateOptions.add("Movie Price");
            updateOptions.add("Movie Director/s");
            updateOptions.add("Movie Description");
            updateOptions.add("Movie Genre");
            updateOptions.add("Movie Casts");
            updateOptions.add("Movie Showing Start Date");
            updateOptions.add("Movie Showing End Date");
            updateOptions.add("Movie Showing Start Time");
            updateOptions.add("Movie Showing End Time");
            updateOptions.add("Movie Theatre");
            updateOptions.add("Movie Language");


            ArrayAdapter<String> adapter = new ArrayAdapter<>(adminhome.this, android.R.layout.simple_spinner_item, updateOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            Spinner updateOptionsSpinner = modalView.findViewById(R.id.update_options);
            updateOptionsSpinner.setAdapter(adapter);


            EditText updateDateTime = modalView.findViewById(R.id.update_datetime);
            EditText updateEditText = modalView.findViewById(R.id.update_edittext);
            LinearLayout editTextContainer = modalView.findViewById(R.id.edit_text_container);


            dialog.show();


            updateOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                    String selectedOption = updateOptionsSpinner.getSelectedItem().toString();


                    editTextContainer.removeAllViews();


                    if (selectedOption.equals("Movie Title") || selectedOption.equals("Movie Price")
                            || selectedOption.equals("Movie Director/s") || selectedOption.equals("Movie Description")
                            || selectedOption.equals("Movie Genre") || selectedOption.equals("Movie Casts")
                            || selectedOption.equals("Movie Theatre") || selectedOption.equals("Movie Language") || selectedOption.equals("Movie Showing Start Date")
                            || selectedOption.equals("Movie Showing End Date") || selectedOption.equals("Movie Showing Start Time") || selectedOption.equals("Movie Showing End Time") || selectedOption.equals("Movie Poster Link")) {


                        updateEditText.setVisibility(View.VISIBLE);
                        editTextContainer.setVisibility(View.VISIBLE);
                        editTextContainer.addView(updateEditText);

                    } else {

                        updateDateTime.setVisibility(View.GONE);
                        editTextContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle if nothing is selected
                }
            });


            Button updateButton = modalView.findViewById(R.id.updateButton);
            updateButton.setOnClickListener(v1 -> {

                String selectedOption = updateOptionsSpinner.getSelectedItem().toString();
                String updatedValue = updateEditText.getText().toString().trim();

                if (updatedValue.isEmpty()) {
                    Toast.makeText(adminhome.this, "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                    return;
                }


                switch (selectedOption) {
                    case "Movie Poster Link":
                        updateMovieDetail(key, "photoLink", updatedValue);
                        break;
                    case "Movie Title":
                        updateMovieDetail(key, "title", updatedValue);
                        break;
                    case "Movie Price":
                        updateMovieDetail(key, "price", updatedValue);
                        break;
                    case "Movie Director/s":
                        updateMovieDetail(key, "director", updatedValue);
                        break;
                    case "Movie Description":
                        updateMovieDetail(key, "description", updatedValue);
                        break;
                    case "Movie Genre":
                        updateMovieDetail(key, "genre", updatedValue);
                        break;
                    case "Movie Casts":
                        updateMovieDetail(key, "casts", updatedValue);
                        break;
                    case "Movie Theatre":
                        updateMovieDetail(key, "theatre", updatedValue);
                        break;
                    case "Movie Language":
                        updateMovieDetail(key, "language", updatedValue);
                        break;
                    case "Movie Showing Start Date":
                        updateMovieDetail(key, "startDate", updatedValue);
                        break;
                    case "Movie Showing End Date":
                        updateMovieDetail(key, "endDate", updatedValue);
                        break;
                    case "Movie Showing Start Time":
                        updateMovieDetail(key, "startTime", updatedValue);
                        break;
                    case "Movie Showing End Time":
                        updateMovieDetail(key, "endTime", updatedValue);
                        break;

                }

                dialog.dismiss();
            });
        });

        deleteView.setOnClickListener(v -> {

            new AlertDialog.Builder(adminhome.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        moviesReference.child(key).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, movie.getTitle() + " deleted.", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to delete " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        adminmovieListLayout.addView(movieItemView);
    }

    private void updateReservationDetail(String key, String field, String updatedValue) {
        DatabaseReference reservationRef = reservationsReference.child(key);
        reservationRef.child(field).setValue(updatedValue)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(adminhome.this, "Reservation updated successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(adminhome.this, "Failed to update reservation.", Toast.LENGTH_SHORT).show();
                });
    }


    private void addReservationToLayout(reservationdata reservation, String key) {
        View reservationItemView = getLayoutInflater().inflate(R.layout.reservation_item, reservationsListLayout, false);
        TextView titleView = reservationItemView.findViewById(R.id.TitleView);
        TextView genreView = reservationItemView.findViewById(R.id.SeatsView);
        ImageView editView = reservationItemView.findViewById(R.id.reservationedit);
        ImageView deleteView = reservationItemView.findViewById(R.id.reservationdelete);

        titleView.setText("Reservation Code: " + reservation.getCode());
        genreView.setText(reservation.getMovie_title());

        editView.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(adminhome.this);


            View modalView = getLayoutInflater().inflate(R.layout.reservation_modal, null);


            builder.setView(modalView);


            AlertDialog dialog = builder.create();


            ArrayList<String> updateOptions = new ArrayList<>();
            updateOptions.add("Choose Details to Update");
            updateOptions.add("Name");
            updateOptions.add("Booking Date");
            updateOptions.add("Contact No");
            updateOptions.add("Email");
            updateOptions.add("Movie Title");
            updateOptions.add("Movie Time");
            updateOptions.add("Reservation Date");
            updateOptions.add("Seat");
            updateOptions.add("Theatre");


            ArrayAdapter<String> adapter = new ArrayAdapter<>(adminhome.this, android.R.layout.simple_spinner_item, updateOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            Spinner updateOptionsSpinner = modalView.findViewById(R.id.update_options);
            updateOptionsSpinner.setAdapter(adapter);


            EditText updateEditText = modalView.findViewById(R.id.update_edittext);
            LinearLayout editTextContainer = modalView.findViewById(R.id.edit_text_container);


            dialog.show();


            updateOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                    String selectedOption = updateOptionsSpinner.getSelectedItem().toString();


                    editTextContainer.removeAllViews();


                    if (selectedOption.equals("Booking Date") || selectedOption.equals("Contact No")
                            || selectedOption.equals("Email") || selectedOption.equals("Movie Title")
                            || selectedOption.equals("Movie Time") || selectedOption.equals("Name")
                            || selectedOption.equals("Reservation Date") || selectedOption.equals("Seat")
                            || selectedOption.equals("Theatre")) {


                        updateEditText.setVisibility(View.VISIBLE);
                        editTextContainer.setVisibility(View.VISIBLE);
                        editTextContainer.addView(updateEditText);

                    } else {

                        editTextContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });

            Button updateButton = modalView.findViewById(R.id.updateButton);
            updateButton.setOnClickListener(v1 -> {

                String selectedOption = updateOptionsSpinner.getSelectedItem().toString();
                String updatedValue = updateEditText.getText().toString().trim();

                if (updatedValue.isEmpty()) {
                    Toast.makeText(adminhome.this, "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                    return;
                }


                switch (selectedOption) {
                    case "Name":
                        updateReservationDetail(key, "name", updatedValue);
                        break;
                    case "Booking Date":
                        updateReservationDetail(key, "booking_date", updatedValue);
                        break;
                    case "Contact No":
                        updateReservationDetail(key, "contact_no", updatedValue);
                        break;
                    case "Email":
                        updateReservationDetail(key, "email", updatedValue);
                        break;
                    case "Movie Title":
                        updateReservationDetail(key, "movie_title", updatedValue);
                        break;
                    case "Movie Time":
                        updateReservationDetail(key, "movie_time", updatedValue);
                        break;
                    case "Reservation Date":
                        updateReservationDetail(key, "reservation_date", updatedValue);
                        break;
                    case "Seat":
                        updateReservationDetail(key, "seat", updatedValue);
                        break;
                    case "Theatre":
                        updateReservationDetail(key, "theatre", updatedValue);
                        break;

                }


                dialog.dismiss();
            });
        });


        deleteView.setOnClickListener(v -> {

            new AlertDialog.Builder(adminhome.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this reservation?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        reservationsReference.orderByChild("code").equalTo(reservation.getCode())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(adminhome.this, "Reservation for " + reservation.getMovie_title() + " deleted.", Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(adminhome.this, "Failed to delete reservation for " + reservation.getMovie_title(), Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        } else {
                                            Toast.makeText(adminhome.this, "Reservation not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(adminhome.this, "Failed to delete reservation: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        reservationsListLayout.addView(reservationItemView);

    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBackPress) {

            super.onBackPressed();
        } else {

        }
    }
}
