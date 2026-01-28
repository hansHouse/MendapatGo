package com.example.mendapatgo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRoomActivity extends AppCompatActivity {

    private static final String TAG = "UpdateRoomActivity";

    private EditText txtRoomNumber;
    private EditText txtRoomType;
    private EditText txtPrice;
    private Spinner spinnerStatus;

    private Room room;
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

        // retrieve room id from intent
        Intent intent = getIntent();
        roomId = intent.getIntExtra("room_id", -1);

        Log.d(TAG, "Opening UpdateRoomActivity for room ID: " + roomId);

        // retrieve room info from database using the room id
        RoomService roomService = ApiUtils.getRoomService();

        // execute the API query
        roomService.getRoom(API_KEY, roomId).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.code() == 200) {
                    // server return success
                    // get room object from response
                    room = response.body();

                    Log.d(TAG, "Room retrieved: " + room.toString());

                    // set values into forms
                    txtRoomNumber.setText(room.getRoomNumber());
                    txtRoomType.setText(room.getRoomType());
                    txtPrice.setText(String.valueOf(room.getPrice()));

                    // set spinner selection
                    String status = room.getStatus();
                    String[] statusArray = getResources().getStringArray(R.array.room_status_array);
                    for (int i = 0; i < statusArray.length; i++) {
                        if (statusArray[i].equalsIgnoreCase(status)) {
                            spinnerStatus.setSelection(i);
                            break;
                        }
                    }
                }
                else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid API key", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, response.toString());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: " + t.getMessage());
                finish();
            }
        });
    }

    /**
     * Update room info in database when the user clicks Update Room button
     * @param view
     */
    public void updateRoom(View view) {
        // get values in form
        String roomNumber = txtRoomNumber.getText().toString().trim();
        String roomType = txtRoomType.getText().toString().trim();
        String priceStr = txtPrice.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

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

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // update the room object with new data
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setPrice(price);
        room.setStatus(status);

        Log.d(TAG, "Updating room: " + room.toString());
        Log.d(TAG, "Sending: ID=" + room.getRoomId() + ", Number=" + roomNumber +
                ", Type=" + roomType + ", Price=" + price + ", Status=" + status);

        // send request to update the room record to the REST API
        RoomService roomService = ApiUtils.getRoomService();

        Call<Room> call = roomService.updateRoom(
                API_KEY,
                room.getRoomId(),
                roomNumber,
                roomType,
                price,
                status
        );

        // execute
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Log.d(TAG, "Update Response: " + response.code());

                if (response.code() == 200) {
                    // server return success code for update request
                    Room updatedRoom = response.body();
                    if (updatedRoom != null) {
                        displayUpdateSuccess("Room " + updatedRoom.getRoomNumber() + " updated successfully.");
                    } else {
                        displayUpdateSuccess("Room updated successfully.");
                    }
                }
                else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid API key", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error " + response.code() + ": " + errorBody);
                        Toast.makeText(getApplicationContext(),
                                "Error " + response.code() + ": " + response.message(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Error: " + response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                displayAlert("Error: " + t.getMessage());
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // end this activity and go back to previous activity
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}