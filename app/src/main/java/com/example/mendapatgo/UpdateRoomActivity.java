package com.example.mendapatgo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRoomActivity extends AppCompatActivity {

    private static final String TAG = "UpdateRoomActivity";

    private EditText txtRoomNumber;
    private EditText txtRoomType;
    private EditText txtPrice;
    private Spinner spinnerStatus;
    private EditText txtUpdatedBy;

    private int roomId;

    // API key for accessing the room service
    private static final String API_KEY = "83417780-aac0-43c8-8367-89821b949be1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_room);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtRoomType = findViewById(R.id.txtRoomType);
        txtPrice = findViewById(R.id.txtPrice);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        txtUpdatedBy = findViewById(R.id.txtUpdatedBy);

        // get room id from intent
        roomId = getIntent().getIntExtra("room_id", 0);

        Log.d(TAG, "Opening UpdateRoomActivity for room ID: " + roomId);

        // fetch room details and populate form
        fetchRoomDetails();
    }

    private void fetchRoomDetails() {
        RoomService roomService = ApiUtils.getRoomService();
        Call<Room> call = roomService.getRoom(API_KEY, roomId);

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.code() == 200) {
                    Room room = response.body();

                    Log.d(TAG, "Room details loaded: " + room.toString());

                    // populate form with room data
                    txtRoomNumber.setText(room.getRoomNumber());
                    txtRoomType.setText(room.getRoomType());
                    txtPrice.setText(String.valueOf(room.getPrice()));

                    // set spinner selection
                    String status = room.getStatus();
                    String[] statusArray = getResources().getStringArray(R.array.room_status_array);
                    for (int i = 0; i < statusArray.length; i++) {
                        if (statusArray[i].equals(status)) {
                            spinnerStatus.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "Error loading room: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplicationContext(), "Error loading room details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "Failed to load room details: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Called when Update Room button is clicked
     * @param v
     */
    public void updateRoom(View v) {
        // get values in form
        String roomNumber = txtRoomNumber.getText().toString().trim();
        String roomType = txtRoomType.getText().toString().trim();
        String priceStr = txtPrice.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String updatedBy = txtUpdatedBy.getText().toString().trim();

        Log.d(TAG, "=== Attempting to Update Room ===");
        Log.d(TAG, "Room ID: " + roomId);
        Log.d(TAG, "Room Number: " + roomNumber);
        Log.d(TAG, "Room Type: " + roomType);
        Log.d(TAG, "Price String: " + priceStr);
        Log.d(TAG, "Status: " + status);
        Log.d(TAG, "Updated By: " + updatedBy);

        // Validate inputs
        if (roomNumber.isEmpty()) {
            Toast.makeText(this, "Please enter room number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (roomType.isEmpty()) {
            Toast.makeText(this, "Please enter room type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedBy.isEmpty()) {
            Toast.makeText(this, "Please enter admin username (who is updating)", Toast.LENGTH_SHORT).show();
            txtUpdatedBy.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            Log.d(TAG, "Parsed Price: " + price);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // send request to update room to the REST API
        RoomService roomService = ApiUtils.getRoomService();
        Call<Room> call = roomService.updateRoom(API_KEY, roomId, roomNumber, roomType, price, status, updatedBy);

        Log.d(TAG, "Sending update request to API...");

        // execute
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Log.d(TAG, "=== Update Response Received ===");
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Message: " + response.message());
                Log.d(TAG, "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // room updated successfully
                    Log.d(TAG, "✅ Room updated successfully");
                    Toast.makeText(getApplicationContext(),
                            "Room updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and go back
                    finish();

                } else if (response.code() == 400) {
                    Log.e(TAG, "❌ ERROR 400: Bad Request");

                    // Try to get error details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                            Toast.makeText(getApplicationContext(),
                                    "Bad Request: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Bad Request: Check all fields are filled correctly",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Bad Request - Please check all fields",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.e(TAG, "❌ ERROR: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplicationContext(),
                            "Error: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "=== Update Request Failed ===");
                Log.e(TAG, "Error: " + t.getMessage());
                t.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Connection error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}