package com.example.mendapatgo;

import android.content.Intent;
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
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRoomActivity extends AppCompatActivity {

    private EditText txtRoomNumber;
    private EditText txtRoomType;
    private EditText txtPrice;
    private Spinner spinnerStatus;
    private EditText txtUpdatedBy;
    private String apiKey; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_room);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get API key from logged-in admin's session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        apiKey = user.getToken(); // Use admin's token as API key

        Log.d("MyApp:", "Using API Key from admin session: " + apiKey);

        // get view objects references
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtRoomType = findViewById(R.id.txtRoomType);
        txtPrice = findViewById(R.id.txtPrice);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        txtUpdatedBy = findViewById(R.id.txtUpdatedBy);
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
     * Called when Add Room button is clicked
     * @param v
     */
    public void addNewRoom(View v) {
        // get values in form
        String roomNumber = txtRoomNumber.getText().toString().trim();
        String roomType = txtRoomType.getText().toString().trim();
        String priceStr = txtPrice.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String updatedBy = txtUpdatedBy.getText().toString().trim();

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
            Toast.makeText(this, "Please enter admin username", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        // send request to add new room to the REST API
        RoomService roomService = ApiUtils.getRoomService();
        Call<Room> call = roomService.addRoom(apiKey, roomNumber, roomType, price, status, updatedBy);

        // execute
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // room added successfully
                    Room addedRoom = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            "Room " + addedRoom.getRoomNumber() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and go back to previous activity
                    finish();
                } else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getMessage());
            }
        });
    }
}