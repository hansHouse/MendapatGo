package com.example.mendapatgo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mendapatgo.model.User;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {

    private TextView tvRoomInfo, tvRoomPrice, tvTotalPrice;
    private EditText etCheckInDate, etCheckOutDate, etGuests;
    private Button btnProceedToPayment;

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private int userId;

    private Calendar checkInCalendar = Calendar.getInstance();
    private Calendar checkOutCalendar = Calendar.getInstance();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize views
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        etCheckInDate = findViewById(R.id.etCheckInDate);
        etCheckOutDate = findViewById(R.id.etCheckOutDate);
        etGuests = findViewById(R.id.etGuests);
        btnProceedToPayment = findViewById(R.id.btnConfirmBooking);

        // Change button text
        btnProceedToPayment.setText("Proceed to Payment");

        // Get room data from intent
        roomId = getIntent().getIntExtra("ROOM_ID", 0);
        roomNumber = getIntent().getStringExtra("ROOM_NUMBER");
        roomType = getIntent().getStringExtra("ROOM_TYPE");
        pricePerNight = getIntent().getDoubleExtra("ROOM_PRICE", 0.0);

        // Get user ID from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        userId = user.getId();

        // Display room information
        tvRoomInfo.setText("Room " + roomNumber + " - " + roomType);
        tvRoomPrice.setText(String.format("RM %.2f per night", pricePerNight));

        // Set up date pickers
        setupDatePickers();

        // Set up payment button
        btnProceedToPayment.setOnClickListener(v -> proceedToPayment());
    }

    private void setupDatePickers() {
        // Check-in date picker
        etCheckInDate.setFocusable(false);
        etCheckInDate.setOnClickListener(v -> showCheckInDatePicker());

        // Check-out date picker
        etCheckOutDate.setFocusable(false);
        etCheckOutDate.setOnClickListener(v -> showCheckOutDatePicker());
    }

    private void showCheckInDatePicker() {
        Calendar minDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    checkInCalendar.set(year, month, dayOfMonth);
                    etCheckInDate.setText(displayFormat.format(checkInCalendar.getTime()));

                    // Reset check-out date if it's before check-in
                    if (etCheckOutDate.getText().toString().isEmpty() ||
                            checkOutCalendar.before(checkInCalendar)) {
                        etCheckOutDate.setText("");
                        checkOutCalendar = (Calendar) checkInCalendar.clone();
                        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    calculateTotalPrice();
                },
                checkInCalendar.get(Calendar.YEAR),
                checkInCalendar.get(Calendar.MONTH),
                checkInCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showCheckOutDatePicker() {
        if (etCheckInDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select check-in date first", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar minDate = (Calendar) checkInCalendar.clone();
        minDate.add(Calendar.DAY_OF_MONTH, 1); // Minimum 1 night stay

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    checkOutCalendar.set(year, month, dayOfMonth);
                    etCheckOutDate.setText(displayFormat.format(checkOutCalendar.getTime()));
                    calculateTotalPrice();
                },
                checkOutCalendar.get(Calendar.YEAR),
                checkOutCalendar.get(Calendar.MONTH),
                checkOutCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void calculateTotalPrice() {
        if (etCheckInDate.getText().toString().isEmpty() ||
                etCheckOutDate.getText().toString().isEmpty()) {
            tvTotalPrice.setText("Total: RM 0.00");
            return;
        }

        try {
            long diffInMillis = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
            long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (numberOfNights <= 0) {
                numberOfNights = 1;
            }

            double totalPrice = numberOfNights * pricePerNight;
            tvTotalPrice.setText(String.format("Total: RM %.2f (%d nights)", totalPrice, numberOfNights));
        } catch (Exception e) {
            Log.e("BookingActivity", "Error calculating price: " + e.getMessage());
        }
    }

    private void proceedToPayment() {
        // Validate inputs
        String checkInDate = etCheckInDate.getText().toString().trim();
        String checkOutDate = etCheckOutDate.getText().toString().trim();
        String guestsStr = etGuests.getText().toString().trim();

        if (checkInDate.isEmpty()) {
            Toast.makeText(this, "Please select check-in date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkOutDate.isEmpty()) {
            Toast.makeText(this, "Please select check-out date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (guestsStr.isEmpty()) {
            Toast.makeText(this, "Please enter number of guests", Toast.LENGTH_SHORT).show();
            return;
        }

        int guests = Integer.parseInt(guestsStr);
        if (guests <= 0) {
            Toast.makeText(this, "Number of guests must be at least 1", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total price
        long diffInMillis = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        double totalPrice = numberOfNights * pricePerNight;

        // Convert dates to yyyy-MM-dd format for API
        String checkInFormatted = dateFormat.format(checkInCalendar.getTime());
        String checkOutFormatted = dateFormat.format(checkOutCalendar.getTime());

        // Show confirmation and proceed to payment
        showBookingConfirmation(checkInFormatted, checkOutFormatted, guests, totalPrice);
    }

    private void showBookingConfirmation(String checkIn, String checkOut, int guests, double totalPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Booking Details");
        builder.setMessage(
                "Room: " + roomNumber + " - " + roomType + "\n" +
                        "Check-in: " + etCheckInDate.getText().toString() + "\n" +
                        "Check-out: " + etCheckOutDate.getText().toString() + "\n" +
                        "Guests: " + guests + "\n" +
                        "Total Price: RM " + String.format("%.2f", totalPrice) + "\n\n" +
                        "Proceed to payment?"
        );

        builder.setPositiveButton("Proceed", (dialog, which) -> {
            // Go to payment activity
            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            intent.putExtra("ROOM_ID", roomId);
            intent.putExtra("ROOM_NUMBER", roomNumber);
            intent.putExtra("ROOM_TYPE", roomType);
            intent.putExtra("CHECK_IN_DATE", checkIn);
            intent.putExtra("CHECK_OUT_DATE", checkOut);
            intent.putExtra("GUESTS", guests);
            intent.putExtra("TOTAL_PRICE", totalPrice);
            startActivity(intent);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}