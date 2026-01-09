package com.example.mendapatgo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MyBookingsActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ListView lvMyBookings;
    private DatabaseHelper db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        tvTitle = findViewById(R.id.tvTitle);
        lvMyBookings = findViewById(R.id.lvMyBookings);

        tvTitle.setText("My Bookings");

        loadMyBookings();
    }

    private void loadMyBookings() {
        Cursor cursor = db.getUserBookings(userId);

        if (cursor.getCount() == 0) {
            tvTitle.setText("No bookings found");
            return;
        }

        String[] from = {"room_number", "check_in_date", "booking_status"};
        int[] to = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                from,
                to,
                0
        ) {
            @Override
            public void setViewText(android.widget.TextView v, String text) {
                Cursor c = getCursor();
                String bookingInfo = "Room: " + c.getString(8) + " - " + c.getString(9) +
                        "\nCheck-in: " + c.getString(3) +
                        "\nCheck-out: " + c.getString(4) +
                        "\nGuests: " + c.getInt(5) +
                        "\nTotal: Rp " + c.getDouble(6) +
                        "\nStatus: " + c.getString(7);
                v.setText(bookingInfo);
            }
        };

        lvMyBookings.setAdapter(adapter);
    }
}