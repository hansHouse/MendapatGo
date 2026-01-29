package com.example.mendapatgo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mendapatgo.model.Booking;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookingService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookingActivity extends AppCompatActivity {

    private static final String TAG = "UpdateBookingActivity";

    private TextView tvBookingId;
    private TextView tvRoomInfo;
    private TextView tvCheckInDate;
    private TextView tvCheckOutDate;
    private TextView tvGuests;
    private Spinner spinnerBookingStatus;
    private Spinner spinnerPaymentStatus;

    private int bookingId;
    private Booking currentBooking;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get API key from logged-in admin's session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        apiKey = user.getToken(); // Use admin's token as API key

        Log.d(TAG, "Using API Key from admin session: " + apiKey);

        // get view objects references
        tvBookingId = findViewById(R.id.tvBookingId);
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvGuests = findViewById(R.id.tvGuests);
        spinnerBookingStatus = findViewById(R.id.spinnerBookingStatus);
        spinnerPaymentStatus = findViewById(R.id.spinnerPaymentStatus);

        // get booking id from intent
        bookingId = getIntent().getIntExtra("booking_id", 0);

        Log.d(TAG, "Opening UpdateBookingActivity for booking ID: " + bookingId);

        // fetch booking details and populate form
        fetchBookingDetails();
    }

    private void fetchBookingDetails() {
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.getBooking(apiKey, bookingId);

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.code() == 200) {
                    // server return success
                    currentBooking = response.body();

                    Log.d(TAG, "Booking retrieved: " + currentBooking.toString());

                    // populate form with booking data
                    tvBookingId.setText("Booking #" + currentBooking.getBookingId());
                    tvRoomInfo.setText("Room " + currentBooking.getRoomId());
                    tvCheckInDate.setText(currentBooking.getCheckInDate());
                    tvCheckOutDate.setText(currentBooking.getCheckOutDate());
                    tvGuests.setText(String.valueOf(currentBooking.getGuests()));

                    // set booking status spinner selection
                    String bookingStatus = currentBooking.getBookingStatus();
                    String[] bookingStatusArray = getResources().getStringArray(R.array.booking_status_array);
                    for (int i = 0; i < bookingStatusArray.length; i++) {
                        if (bookingStatusArray[i].equalsIgnoreCase(bookingStatus)) {
                            spinnerBookingStatus.setSelection(i);
                            break;
                        }
                    }

                    // set payment status spinner selection
                    String paymentStatus = currentBooking.getPaymentStatus();
                    String[] paymentStatusArray = getResources().getStringArray(R.array.payment_status_array);
                    for (int i = 0; i < paymentStatusArray.length; i++) {
                        if (paymentStatusArray[i].equalsIgnoreCase(paymentStatus)) {
                            spinnerPaymentStatus.setSelection(i);
                            break;
                        }
                    }
                }
                else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, response.toString());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: " + t.getMessage());
                finish();
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

    /**
     * Called when Update Booking button is clicked
     * @param v
     */
    public void updateBooking(View v) {
        // get values from spinners
        String bookingStatus = spinnerBookingStatus.getSelectedItem().toString();
        String paymentStatus = spinnerPaymentStatus.getSelectedItem().toString();

        Log.d(TAG, "Updating booking: " + bookingId);
        Log.d(TAG, "Sending: Booking Status=" + bookingStatus + ", Payment Status=" + paymentStatus);

        // send request to update booking to the REST API
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.updateBookingStatus(apiKey, bookingId, bookingStatus, paymentStatus);

        // execute
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                Log.d(TAG, "Update Response: " + response.code());

                if (response.code() == 200) {
                    // booking updated successfully
                    Booking updatedBooking = response.body();
                    if (updatedBooking != null) {
                        displayUpdateSuccess("Booking #" + updatedBooking.getBookingId() + " updated successfully.");
                    } else {
                        displayUpdateSuccess("Booking updated successfully.");
                    }
                }
                else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error " + response.code() + ": " + errorBody);
                        Toast.makeText(getApplicationContext(),
                                "Error " + response.code() + ": " + response.message(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Error: " + response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                displayAlert("Error: " + t.getMessage());
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button for update success
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // end this activity and go back to previous activity
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}