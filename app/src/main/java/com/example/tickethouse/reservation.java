package com.example.tickethouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class reservation extends AppCompatActivity {

    private GridLayout datesGridLayout;  // GridLayout for date buttons
    private GridLayout seatsGridLayout;  // GridLayout for seat buttons
    private Spinner theatreSpinner;     // Spinner for theater selection
    private Spinner paymentSpinner;     // Spinner for payment method selection
    private TextView dateMonth;         // TextView for displaying the month(s)
    private RadioGroup paymentOptions;  // RadioGroup for payment options
    private Button selectedDateButton;
    private final List<String> selectedSeats = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference reservationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        database = FirebaseDatabase.getInstance();
        reservationsRef = database.getReference("reservations");

        // Initialize components
        datesGridLayout = findViewById(R.id.datesGridLayout);
        seatsGridLayout = findViewById(R.id.seatsGridLayout);
        theatreSpinner = findViewById(R.id.theatreSpinner);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        dateMonth = findViewById(R.id.dateMonth);
        paymentOptions = findViewById(R.id.EpaymentOptions);

        // Retrieve intent data
        String movieStartDate = getIntent().getStringExtra("movieStartDate");
        String movieEndDate = getIntent().getStringExtra("movieEndDate");
        String movieTheatres = getIntent().getStringExtra("movieTheatres");
        String movieTitle = getIntent().getStringExtra("movieTitle");
        TextView reserveTitle = findViewById(R.id.reserveTitle);
        String movieStartTime = getIntent().getStringExtra("movieStartTime");
        String movieEndTime = getIntent().getStringExtra("movieEndTime");
        TextView showingTime = findViewById(R.id.showingTime);

        if (movieStartTime != null && movieEndTime != null) {
            String formattedTime = movieStartTime + " - " + movieEndTime;
            showingTime.setText(formattedTime);
        } else {
            showingTime.setText("Time Not Available");
        }

        if (movieStartDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date startDate = sdf.parse(movieStartDate);
                Date endDate = movieEndDate != null ? sdf.parse(movieEndDate) : null;
                populateDateButtons(startDate, endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (movieTheatres != null) {
            String[] theatersArray = movieTheatres.split(",");
            populateTheatreSpinner(theatersArray);
        }

        if (movieTitle != null) {
            reserveTitle.setText(movieTitle);
        } else {
            reserveTitle.setText("No Title Available");
        }



        setupPaymentSpinner();

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> saveReservation());
    }

    private String getSelectedDate() {
        if (selectedDateButton != null) {
            return selectedDateButton.getTag().toString(); // Return the full date stored in the tag
        }
        return null; // No date selected
    }

    private String getSelectedSeats() {
        return String.join(", ", selectedSeats); // Return seats as a comma-separated string
    }

    private void saveReservation() {
        String theatre = theatreSpinner.getSelectedItem().toString();
        String reservationDate = getSelectedDate(); // Method to get selected date
        String seats = getSelectedSeats(); // Method to get selected seats
        String name = ((TextView) findViewById(R.id.Name)).getText().toString();
        String email = ((TextView) findViewById(R.id.EName)).getText().toString();
        String contactNo = ((TextView) findViewById(R.id.number)).getText().toString();
        String paymentMethod = paymentSpinner.getSelectedItem().toString();
        String bookingDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        // Retrieve movie title and time
        String movieTitle = ((TextView) findViewById(R.id.reserveTitle)).getText().toString();
        String movieTime = ((TextView) findViewById(R.id.showingTime)).getText().toString();

        if (theatre.isEmpty() || reservationDate == null || seats.isEmpty() || name.isEmpty() ||
                email.isEmpty() || contactNo.isEmpty() || paymentMethod.isEmpty() || movieTitle.isEmpty() || movieTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, check if any individual seat in the selected reservation already exists for the same movie title and date
        reservationsRef.orderByChild("reservation_date").equalTo(reservationDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String existingTitle = snapshot.child("movie_title").getValue(String.class);
                            String existingSeats = snapshot.child("seat").getValue(String.class);

                            if (existingTitle != null && existingTitle.equals(movieTitle) && existingSeats != null) {
                                String[] existingSeatArray = existingSeats.split(", ");
                                String[] selectedSeatArray = seats.split(", ");

                                for (String selectedSeat : selectedSeatArray) {
                                    for (String existingSeat : existingSeatArray) {
                                        if (existingSeat.equals(selectedSeat)) {
                                            Toast.makeText(reservation.this, "The seat is already reserved for the selected date and movie.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        // Generate a unique reservation ID
                        String reservationId = "reservation" + System.currentTimeMillis();

                        // Generate a random 5-digit code
                        int randomCode = (int) (Math.random() * 90000) + 10000;

                        // Create reservation object
                        Map<String, String> reservation = new HashMap<>();
                        reservation.put("theatre", theatre);
                        reservation.put("name", name);
                        reservation.put("seat", seats);
                        reservation.put("reservation_date", reservationDate);
                        reservation.put("booking_date", bookingDate);
                        reservation.put("email", email);
                        reservation.put("contact_no", contactNo);
                        reservation.put("payment_method", paymentMethod);
                        reservation.put("movie_title", movieTitle);
                        reservation.put("movie_time", movieTime);
                        reservation.put("code", String.valueOf(randomCode)); // Add the generated code

                        // Save to Firebase
                        reservationsRef.child(reservationId).setValue(reservation).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(reservation.this, "Reservation saved successfully!", Toast.LENGTH_SHORT).show();

                                // Pass relevant data to ticket activity
                                Intent intent = new Intent(reservation.this, ticket.class);
                                intent.putExtra("movieTitle", movieTitle);
                                intent.putExtra("movieTime", movieTime);
                                intent.putExtra("theatre", theatre);
                                intent.putExtra("reservationDate", reservationDate);
                                intent.putExtra("seats", seats);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("contactNo", contactNo);
                                intent.putExtra("paymentMethod", paymentMethod);
                                intent.putExtra("bookingDate", bookingDate);
                                intent.putExtra("code", randomCode); // Pass the code to the next activity

                                // Start ticket activity
                                startActivity(intent);

                                // Close current activity
                                finish();
                            } else {
                                Toast.makeText(reservation.this, "Failed to save reservation.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(reservation.this, "Error checking existing reservations.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkReservedSeats(String theatre, String reservationDate) {
        String movieTitle = ((TextView) findViewById(R.id.reserveTitle)).getText().toString(); // Get the current movie title

        reservationsRef.orderByChild("reservation_date").equalTo(reservationDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // List to store all reserved seats for the selected date, theater, and movie
                        List<String> reservedSeats = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String reservedMovieTitle = snapshot.child("movie_title").getValue(String.class);
                            String reservedTheatre = snapshot.child("theatre").getValue(String.class);
                            String seats = snapshot.child("seat").getValue(String.class);

                            // Match by movie title, theater, and ensure seats are not null
                            if (movieTitle.equals(reservedMovieTitle) && theatre.equals(reservedTheatre) && seats != null) {
                                String[] seatsArray = seats.split(", ");
                                reservedSeats.addAll(Arrays.asList(seatsArray));
                            }
                        }

                        // Populate seats and mark the reserved ones
                        populateSeats(theatre, reservedSeats);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(reservation.this, "Error fetching reserved seats.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupPaymentSpinner() {
        String[] paymentOptionsArray = {"Payment Center / E-Wallet", "Credit / Debit Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentOptionsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(adapter);

        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = paymentOptionsArray[position];
                if (selectedOption.equals("Payment Center / E-Wallet")) {
                    paymentOptions.setVisibility(View.VISIBLE); // Show RadioGroup
                    showPaymentOptions();
                } else if (selectedOption.equals("Credit / Debit Card")) {
                    paymentOptions.setVisibility(View.GONE); // Hide RadioGroup
                    showCreditCardModal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
        });
    }


    private void showPaymentOptions() {
        paymentOptions.removeAllViews();

        // Add radio buttons dynamically
        String[] options = {"GCash", "Maya", "PayPal"};
        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTextColor(getResources().getColor(android.R.color.white));
            paymentOptions.addView(radioButton);
        }
    }

    private void showCreditCardModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.credit_card_modal, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        Button confirmButton = dialogView.findViewById(R.id.confirmCreditCardButton);

        confirmButton.setOnClickListener(v -> {
            Toast.makeText(reservation.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void populateDateButtons(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        datesGridLayout.setColumnCount(5); // 5 columns as required
        datesGridLayout.removeAllViews();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder monthsInvolved = new StringBuilder();
        String previousMonth = null;

        for (int i = 0; i < 15; i++) {
            Button button = new Button(this);
            String fullDate = dateFormat.format(calendar.getTime());
            button.setTag(fullDate); // Store the full date in the tag
            button.setText(new SimpleDateFormat("d").format(calendar.getTime())); // Display day only
            button.setBackground(getDrawable(R.drawable.datesbtn));
            button.setGravity(Gravity.CENTER);
            button.setTextSize(10);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) (getResources().getDisplayMetrics().density * 60);
            int marginLeft = (int) (3 * getResources().getDisplayMetrics().density);
            params.leftMargin = marginLeft;

            int row = i / 5;
            int column = i % 5;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(column);
            button.setLayoutParams(params);

            // OnClickListener to handle date selection
            button.setOnClickListener(v -> {
                if (selectedDateButton != null) {
                    selectedDateButton.setBackground(getDrawable(R.drawable.datesbtn)); // Reset previous button
                }
                button.setBackgroundColor(Color.parseColor("#f7b7b5")); // Highlight selected
                selectedDateButton = button; // Track the selected button

                // Call checkReservedSeats to fetch reserved seats for the selected date
                String selectedTheater = theatreSpinner.getSelectedItem().toString();
                checkReservedSeats(selectedTheater, button.getTag().toString());
            });


            datesGridLayout.addView(button);

            String currentMonth = monthFormat.format(calendar.getTime());
            if (!currentMonth.equals(previousMonth)) {
                if (monthsInvolved.length() > 0) {
                    monthsInvolved.append(" - ");
                }
                monthsInvolved.append(currentMonth);
            }
            previousMonth = currentMonth;

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        dateMonth.setText(monthsInvolved.toString());
    }

    private void populateSeats(String theater, List<String> reservedSeats) {
        seatsGridLayout.removeAllViews();
        int seatCount = theater.contains("VIP") ? 20 : 30; // Example logic for seat count
        int rows = seatCount / 5;
        char rowLetter = 'A';

        for (int i = 0; i < seatCount; i++) {
            Button seatButton = new Button(this);
            String seatLabel = rowLetter + String.valueOf((i % 5) + 1);
            seatButton.setTag(seatLabel); // Store seat label in the tag
            seatButton.setText(seatLabel);
            seatButton.setBackground(getDrawable(R.drawable.datesbtn));
            seatButton.setGravity(Gravity.CENTER);
            seatButton.setTextSize(8);

            // Set background color based on whether the seat is reserved or selected
            if (reservedSeats.contains(seatLabel)) {
                seatButton.setBackgroundColor(Color.parseColor("#ab9899")); // Highlight reserved seats
                seatButton.setEnabled(false);  // Disable the button to prevent further selection
            } else {
                seatButton.setBackground(getDrawable(R.drawable.datesbtn)); // Regular background for available seats
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) (getResources().getDisplayMetrics().density * 60);
            int marginLeft = (int) (3 * getResources().getDisplayMetrics().density);
            params.leftMargin = marginLeft;

            int row = i / 5;
            int column = i % 5;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(column);
            seatButton.setLayoutParams(params);

            // OnClickListener to handle seat selection (only for available seats)
            seatButton.setOnClickListener(v -> {
                if (seatButton.isEnabled()) {
                    if (selectedSeats.contains(seatLabel)) {
                        // Deselect seat
                        selectedSeats.remove(seatLabel);
                        seatButton.setBackground(getDrawable(R.drawable.datesbtn)); // Reset background
                    } else {
                        // Select seat
                        selectedSeats.add(seatLabel);
                        seatButton.setBackgroundColor(Color.parseColor("#f7b7b5")); // Highlight selected seat
                    }
                }
            });

            seatsGridLayout.addView(seatButton);

            // Update row letter after every 5 seats
            if ((i % 5) == 4) {
                rowLetter++;
            }
        }
    }



    private void populateTheatreSpinner(String[] theatersArray) {
        // Set up the ArrayAdapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theatersArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theatreSpinner.setAdapter(adapter);

        // Set a listener to handle theater selection
        theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedTheater = theatersArray[position].trim();

                // Fetch reserved seats for the selected theater
                checkReservedSeats(selectedTheater, getSelectedDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected (optional)
            }
        });
    }

}
