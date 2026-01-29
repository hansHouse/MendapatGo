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
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRoomActivity extends AppCompatActivity {

    private static final String TAG = "UpdateRoomActivity";

    private EditText txtRoomNumber;
    private EditText txtRoomType;
    private EditText txtPrice;
    private Spinner spinnerStatus;

    private Room room; // current room to be updated
    private int roomId;
    private String apiKey;

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

        // Get API key from logged-in admin's session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        apiKey = user.getToken(); // Use admin's token as API key

        Log.d(TAG, "Using API Key from admin session: " + apiKey);

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
        roomService.getRoom(apiKey, roomId).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Log.d(TAG, "Update Form Populate Response: " + response.raw().toString());

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
                    // Unauthorized - invalid or expired token
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
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
     * Clear session and redirect to login
     */
    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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

        // set updated_at to current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updated_at = sdf.format(new Date());

        Log.d(TAG, "Old Room info: " + room.toString());

        // update the room object with new data
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setPrice(price);
        room.setStatus(status);

        Log.d(TAG, "New Room info: " + room.toString());
        Log.d(TAG, "Sending updated_at: " + updated_at);

        // send request to update the room record to the REST API
        RoomService roomService = ApiUtils.getRoomService();
        Call<Room> call = roomService.updateRoom(
                apiKey,
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPrice(),
                room.getStatus()
        );

        // execute
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Log.d(TAG, "Update Request Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success code for update request
                    Room updatedRoom = response.body();
                    displayUpdateSuccess("Room " + updatedRoom.getRoomNumber() + " updated successfully.");
                }
                else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
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
                Log.d(TAG, "Error: " + t.getMessage());
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