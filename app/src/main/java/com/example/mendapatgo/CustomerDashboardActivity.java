package com.example.mendapatgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ListView lvRooms;
    private Button btnLogout, btnMyBookings;
    private DatabaseHelper db;
    private int userId;
    private ArrayList<Integer> roomIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        db = new DatabaseHelper(this);
        roomIds = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        userId = prefs.getInt("userId", -1);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvRooms = findViewById(R.id.lvRooms);
        btnLogout = findViewById(R.id.btnLogout);
        btnMyBookings = findViewById(R.id.btnMyBookings);

        tvWelcome.setText("Welcome, " + username + "!");

        loadAvailableRooms();

        lvRooms.setOnItemClickListener((parent, view, position, id) -> {
            if (position < roomIds.size()) {
                Cursor cursor = db.getAvailableRooms();
                cursor.moveToPosition(position);

                int roomId = cursor.getInt(0);
                String roomNumber = cursor.getString(1);
                String roomType = cursor.getString(2);
                double price = cursor.getDouble(3);

                Intent intent = new Intent(CustomerDashboardActivity.this, BookingActivity.class);
                intent.putExtra("roomId", roomId);
                intent.putExtra("roomNumber", roomNumber);
                intent.putExtra("roomType", roomType);
                intent.putExtra("price", price);
                intent.putExtra("userId", userId);
                startActivity(intent);

                cursor.close();
            }
        });

        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, MyBookingsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAvailableRooms();
    }

    private void loadAvailableRooms() {
        Cursor cursor = db.getAvailableRooms();
        ArrayList<String> roomList = new ArrayList<>();
        roomIds.clear();

        if (cursor.moveToFirst()) {
            do {
                roomIds.add(cursor.getInt(0));
                String roomInfo = "Room " + cursor.getString(1) + " - " +
                        cursor.getString(2) + " - Rp " + cursor.getDouble(3);
                roomList.add(roomInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if (roomList.isEmpty()) {
            roomList.add("No available rooms");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, roomList);
        lvRooms.setAdapter(adapter);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(CustomerDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}