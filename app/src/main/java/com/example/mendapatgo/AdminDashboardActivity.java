package com.example.mendapatgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnManageRooms, btnManageBookings, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "Admin");

        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnManageBookings = findViewById(R.id.btnManageBookings);
        btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + username + "!");

        btnManageRooms.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageRoomsActivity.class);
            startActivity(intent);
        });

        btnManageBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageBookingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}