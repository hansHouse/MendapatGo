package com.example.mendapatgo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ManageRoomsActivity extends AppCompatActivity {

    private ListView lvRooms;
    private Button btnAddRoom;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        db = new DatabaseHelper(this);

        lvRooms = findViewById(R.id.lvRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        loadRooms();

        btnAddRoom.setOnClickListener(v -> showAddRoomDialog());
    }

    private void loadRooms() {
        Cursor cursor = db.getAllRooms();

        String[] from = {"room_number", "room_type", "price", "status"};
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
                String roomInfo = "Room " + c.getString(1) + " - " +
                        c.getString(2) + " - Rp " + c.getDouble(3) +
                        " - " + c.getString(4);
                v.setText(roomInfo);
            }
        };

        lvRooms.setAdapter(adapter);
    }

    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Room");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_add_room, null);
        EditText etRoomNumber = view.findViewById(R.id.etRoomNumber);
        EditText etRoomType = view.findViewById(R.id.etRoomType);
        EditText etPrice = view.findViewById(R.id.etPrice);

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String roomNumber = etRoomNumber.getText().toString().trim();
            String roomType = etRoomType.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (roomNumber.isEmpty() || roomType.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            boolean result = db.addRoom(roomNumber, roomType, price);

            if (result) {
                Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
                loadRooms();
            } else {
                Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}