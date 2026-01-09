package com.example.mendapatgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView tvRoomInfo;
    private EditText etCheckIn, etCheckOut, etGuests;
    private Button btnConfirmBooking;
    private DatabaseHelper db;

    private int roomId, userId;
    private double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = new DatabaseHelper(this);

        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        etGuests = findViewById(R.id.etGuests);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // Get room details from intent
        roomId = getIntent().getIntExtra("roomId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        String roomNumber = getIntent().getStringExtra("roomNumber");
        String roomType = getIntent().getStringExtra("roomType");
        price = getIntent().getDoubleExtra("price", 0);

        tvRoomInfo.setText("Room: " + roomNumber + "\nType: " + roomType + "\nPrice: Rp " + price + "/night");

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void confirmBooking() {
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();
        String guestsStr = etGuests.getText().toString().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty() || guestsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int guests = Integer.parseInt(guestsStr);

        // Simple calculation (in real app, calculate days between dates)
        double totalPrice = price * 1; // Assuming 1 night for simplicity

        boolean result = db.createBooking(userId, roomId, checkIn, checkOut, guests, totalPrice);

        if (result) {
            Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Booking failed", Toast.LENGTH_SHORT).show();
        }
    }
}