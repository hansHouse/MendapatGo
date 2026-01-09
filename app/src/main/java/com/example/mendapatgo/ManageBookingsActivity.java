package com.example.mendapatgo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ManageBookingsActivity extends AppCompatActivity {

    private ListView lvBookings;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        db = new DatabaseHelper(this);

        lvBookings = findViewById(R.id.lvBookings);

        loadBookings();

        lvBookings.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int bookingId = cursor.getInt(0);
            String currentStatus = cursor.getString(7);

            showUpdateStatusDialog(bookingId, currentStatus);
        });
    }

    private void loadBookings() {
        Cursor cursor = db.getAllBookings();

        String[] from = {"username", "room_number", "check_in_date", "booking_status"};
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
                String bookingInfo = "Customer: " + c.getString(8) +
                        "\nRoom: " + c.getString(9) +
                        "\nCheck-in: " + c.getString(3) +
                        "\nCheck-out: " + c.getString(4) +
                        "\nGuests: " + c.getInt(5) +
                        "\nTotal: Rp " + c.getDouble(6) +
                        "\nStatus: " + c.getString(7);
                v.setText(bookingInfo);
            }
        };

        lvBookings.setAdapter(adapter);
    }

    private void showUpdateStatusDialog(int bookingId, String currentStatus) {
        String[] statuses = {"pending", "confirmed", "cancelled", "completed"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Booking Status");
        builder.setItems(statuses, (dialog, which) -> {
            String newStatus = statuses[which];
            boolean result = db.updateBookingStatus(bookingId, newStatus);

            if (result) {
                Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                loadBookings();
            } else {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}