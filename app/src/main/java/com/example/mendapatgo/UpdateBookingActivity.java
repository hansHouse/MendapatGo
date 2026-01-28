package com.example.mendapatgo;

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
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookingService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookingActivity extends AppCompatActivity {

    private TextView tvBookingId;
    private TextView tvRoomInfo;
    private TextView tvCheckInDate;
    private TextView tvCheckOutDate;
    private TextView tvGuests;
    private Spinner spinnerBookingStatus;
    private Spinner spinnerPaymentStatus;

    private int bookingId;

    // API key for accessing the booking service
    private static final String API_KEY = "83417780-aac0-43c8-8367-89821b949be1";

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

        // fetch booking details and populate form
        fetchBookingDetails();
    }

    private void fetchBookingDetails() {
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.getBooking(API_KEY, bookingId);

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.code() == 200) {
                    Booking booking = response.body();

                    // populate form with booking data
                    tvBookingId.setText("Booking #" + booking.getBookingId());
                    tvRoomInfo.setText("Room " + booking.getRoomId()); // You may want to fetch room details
                    tvCheckInDate.setText(booking.getCheckInDate());
                    tvCheckOutDate.setText(booking.getCheckOutDate());
                    tvGuests.setText(String.valueOf(booking.getGuests()));

                    // set booking status spinner selection
                    String bookingStatus = booking.getBookingStatus();
                    String[] bookingStatusArray = getResources().getStringArray(R.array.booking_status_array);
                    for (int i = 0; i < bookingStatusArray.length; i++) {
                        if (bookingStatusArray[i].equals(bookingStatus)) {
                            spinnerBookingStatus.setSelection(i);
                            break;
                        }
                    }

                    // set payment status spinner selection
                    String paymentStatus = booking.getPaymentStatus();
                    String[] paymentStatusArray = getResources().getStringArray(R.array.payment_status_array);
                    for (int i = 0; i < paymentStatusArray.length; i++) {
                        if (paymentStatusArray[i].equals(paymentStatus)) {
                            spinnerPaymentStatus.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error loading booking details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Called when Update Booking button is clicked
     * @param v
     */
    public void updateBooking(View v) {
        // get values from spinners
        String bookingStatus = spinnerBookingStatus.getSelectedItem().toString();
        String paymentStatus = spinnerPaymentStatus.getSelectedItem().toString();

        // send request to update booking to the REST API
        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.updateBookingStatus(API_KEY, bookingId, bookingStatus, paymentStatus);

        // execute
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // booking updated successfully
                    Toast.makeText(getApplicationContext(),
                            "Booking updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and go back
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                Log.d("MyApp:", "Error: " + t.getMessage());
            }
        });
    }
}