package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mendapatgo.model.User;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout, btnViewRooms, btnMyBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);

        // Check if the root view ID is "main" in your XML
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 1. Initialize UI components from the XML
        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewRooms = findViewById(R.id.btnViewRooms);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnLogout = findViewById(R.id.btnLogout);

        // 2. Set Welcome Message
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        if (user != null) {
            tvWelcome.setText("Welcome, " + user.getUsername() + "!");
        } else {
            clearSessionAndRedirect();
        }

        // 3. Set Button Actions

        // Open room list when user clicks "View Available Rooms"
        btnViewRooms.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoomListActivity.class);
            startActivity(intent);
        });

        // Open My Bookings activity (CORRECTED)
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyBookingActivity.class);
            startActivity(intent);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> clearSessionAndRedirect());
    }

    public void clearSessionAndRedirect() {
        new SharedPrefManager(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}