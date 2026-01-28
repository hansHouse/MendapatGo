package com.example.mendapatgo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.adapter.BookingAdapter;
import com.example.mendapatgo.model.BookingResponse;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private ProgressBar progressBar;
    private TextView tvNoBookings;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        // Initialize views
        rvBookings = findViewById(R.id.rvBookings);
        progressBar = findViewById(R.id.progressBar);
        tvNoBookings = findViewById(R.id.tvNoBookings);

        // Set up RecyclerView
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Load bookings
        loadMyBookings();
    }

    private void loadMyBookings() {
        progressBar.setVisibility(View.VISIBLE);
        rvBookings.setVisibility(View.GONE);
        tvNoBookings.setVisibility(View.GONE);

        // Get user details
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        if (user == null || user.getId() <= 0) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String token = user.getToken();
        int userId = user.getId();

        // Call API to get bookings
        BookService bookService = ApiUtils.getBookService();
        Call<List<BookingResponse>> call = bookService.getUserBookings(token, userId);

        call.enqueue(new Callback<List<BookingResponse>>() {
            @Override
            public void onResponse(Call<List<BookingResponse>> call, Response<List<BookingResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<BookingResponse> bookings = response.body();

                    if (bookings.isEmpty()) {
                        tvNoBookings.setVisibility(View.VISIBLE);
                        tvNoBookings.setText("You have no bookings yet.\nStart by booking a room!");
                    } else {
                        rvBookings.setVisibility(View.VISIBLE);
                        adapter = new BookingAdapter(MyBookingActivity.this, bookings);
                        rvBookings.setAdapter(adapter);
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(MyBookingActivity.this,
                            "Session expired. Please login again.",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 404) {
                    tvNoBookings.setVisibility(View.VISIBLE);
                    tvNoBookings.setText("No bookings found.");
                } else {
                    tvNoBookings.setVisibility(View.VISIBLE);
                    tvNoBookings.setText("Error loading bookings: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<BookingResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvNoBookings.setVisibility(View.VISIBLE);
                tvNoBookings.setText("Connection error: " + t.getMessage());
                Log.e("MyBookingsActivity", "Failed to load bookings", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bookings when returning to this activity
        loadMyBookings();
    }
}