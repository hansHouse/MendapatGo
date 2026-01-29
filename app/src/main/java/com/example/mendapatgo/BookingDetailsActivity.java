package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mendapatgo.model.Booking;
import com.example.mendapatgo.model.BookingResponse;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookingService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BookingDetailsActivity";

    private TextView tvBookingId, tvRoomInfo, tvCheckInDate, tvCheckOutDate,
            tvGuests, tvTotalPrice, tvBookingStatus, tvPaymentMethod, tvPaymentStatus;
    private Button btnCancelBooking;

    private int bookingId;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        // Get API key from logged-in user's session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        apiKey = user.getToken(); // Use user's token as API key

        // Initialize views
        tvBookingId = findViewById(R.id.tvBookingId);
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvGuests = findViewById(R.id.tvGuests);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);

        // Get booking data from intent
        loadBookingDetails();

        // Set up cancel button
        btnCancelBooking.setOnClickListener(v -> showCancelConfirmation());
    }

    private void loadBookingDetails() {
        BookingResponse booking = (BookingResponse) getIntent().getSerializableExtra("BOOKING");

        if (booking == null) {
            Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingId = booking.getBooking_id();

        // Log the booking data to debug
        Log.d(TAG, "Booking ID: " + booking.getBooking_id());
        Log.d(TAG, "Room Number: " + booking.getRoom_number());
        Log.d(TAG, "Room Type: " + booking.getRoom_type());
        Log.d(TAG, "Room ID: " + booking.getRoom_id());

        // Display booking details
        tvBookingId.setText("Booking #" + booking.getBooking_id());

        // Handle room information with proper null checks
        String roomInfo = buildRoomInfo(booking);
        tvRoomInfo.setText(roomInfo);

        tvCheckInDate.setText(booking.getCheck_in_date() != null ?
                booking.getCheck_in_date() : "N/A");
        tvCheckOutDate.setText(booking.getCheck_out_date() != null ?
                booking.getCheck_out_date() : "N/A");
        tvGuests.setText(String.valueOf(booking.getGuests()));
        tvTotalPrice.setText(String.format("RM %.2f", booking.getTotal_price()));
        tvBookingStatus.setText(booking.getBooking_status() != null ?
                booking.getBooking_status() : "Pending");
        tvPaymentMethod.setText(booking.getBooking_method() != null ?
                booking.getBooking_method() : "N/A");
        tvPaymentStatus.setText(booking.getPayment_status() != null ?
                booking.getPayment_status() : "Unpaid");

        // Set status color
        setStatusColor(booking.getBooking_status());

        // Disable cancel button if already cancelled or completed
        String status = booking.getBooking_status();
        if (status != null && (status.equalsIgnoreCase("cancelled") ||
                status.equalsIgnoreCase("completed") ||
                status.equalsIgnoreCase("checked-in"))) {
            btnCancelBooking.setEnabled(false);
            btnCancelBooking.setText("Cannot Cancel");
        }
    }

    /**
     * Build room information string with proper null handling
     */
    private String buildRoomInfo(BookingResponse booking) {
        String roomNumber = booking.getRoom_number();
        String roomType = booking.getRoom_type();
        int roomId = booking.getRoom_id();

        // If both room number and type are available
        if (roomNumber != null && !roomNumber.isEmpty() &&
                roomType != null && !roomType.isEmpty()) {
            return "Room " + roomNumber + " - " + roomType;
        }

        // If only room number is available
        if (roomNumber != null && !roomNumber.isEmpty()) {
            return "Room " + roomNumber;
        }

        // If only room type is available
        if (roomType != null && !roomType.isEmpty()) {
            return roomType;
        }

        // Fallback to room ID
        return "Room ID: " + roomId;
    }

    private void setStatusColor(String status) {
        if (status == null) return;

        int color;
        switch (status.toLowerCase()) {
            case "confirmed":
                color = getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "pending":
                color = getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "cancelled":
                color = getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "completed":
                color = getResources().getColor(android.R.color.holo_blue_dark);
                break;
            default:
                color = getResources().getColor(android.R.color.darker_gray);
        }
        tvBookingStatus.setTextColor(color);
    }

    private void showCancelConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking?");
        builder.setMessage("Are you sure you want to cancel this booking? This action cannot be undone.");

        builder.setPositiveButton("Yes, Cancel", (dialog, which) -> {
            cancelBooking();
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void cancelBooking() {
        btnCancelBooking.setEnabled(false);
        btnCancelBooking.setText("Cancelling...");

        BookingService bookingService = ApiUtils.getBookingService();

        // Call API to update booking status to cancelled
        Call<Booking> call = bookingService.updateBookingStatus(
                apiKey,  // Use dynamic API key
                bookingId,
                "Cancelled",
                "Pending"  // Keep payment status as is
        );

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call,
                                   Response<Booking> response) {
                if (response.isSuccessful()) {
                    showCancellationSuccess();
                } else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Toast.makeText(BookingDetailsActivity.this,
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    btnCancelBooking.setEnabled(true);
                    btnCancelBooking.setText("Cancel Booking");
                    Toast.makeText(BookingDetailsActivity.this,
                            "Failed to cancel booking: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                btnCancelBooking.setEnabled(true);
                btnCancelBooking.setText("Cancel Booking");
                Toast.makeText(BookingDetailsActivity.this,
                        "Connection error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Clear session and redirect to login
     */
    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showCancellationSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Booking Cancelled");
        builder.setMessage("Your booking has been cancelled successfully.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}