package com.example.mendapatgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.adapter.RoomAdapter;
import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomListActivity extends AppCompatActivity {
    private RoomService roomService;
    private RecyclerView rvRoomList;
    private RoomAdapter adapter;
    private String apiKey; // Dynamic API key from logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get API key from logged-in user's session
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        apiKey = user.getToken(); // Use user's token as API key

        Log.d("MyApp:", "Using API Key from user session: " + apiKey);

        // get reference to the RecyclerView roomList
        rvRoomList = findViewById(R.id.rvRoomList);
        // register for context menu
        registerForContextMenu(rvRoomList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // fetch and update room list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        Log.d("MyApp:", "========== Starting API Call ==========");
        Log.d("MyApp:", "API Key: " + apiKey);

        // get room service instance
        roomService = ApiUtils.getRoomService();

        // SENDING the API key!
        roomService.getAllRooms(apiKey).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    Log.d("MyApp:", "SUCCESS! Response code 200");

                    // Get list of room object from response
                    List<Room> rooms = response.body();

                    if (rooms != null && !rooms.isEmpty()) {
                        Log.d("MyApp:", " Loaded " + rooms.size() + " rooms");

                        // initialize adapter
                        adapter = new RoomAdapter(getApplicationContext(), rooms);

                        // set adapter to the RecyclerView
                        rvRoomList.setAdapter(adapter);

                        // set layout to recycler view
                        rvRoomList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        // add separator between item in the list
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                                rvRoomList.getContext(),
                                DividerItemDecoration.VERTICAL);
                        rvRoomList.addItemDecoration(dividerItemDecoration);

                        Toast.makeText(getApplicationContext(),
                                "Loaded " + rooms.size() + " rooms!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("MyApp:", "Room list is empty or null");
                        Toast.makeText(getApplicationContext(),
                                "No rooms available in database",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else if (response.code() == 401) {
                    // Unauthorized - invalid or expired token
                    Log.e("MyApp:", "ERROR 401: Unauthorized - Invalid API key");
                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Log.e("MyApp:", "ERROR " + response.code() + ": " + response.message());
                    Toast.makeText(getApplicationContext(),
                            "Error " + response.code() + ": " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("MyApp:", "Connection failed: " + t.getMessage());
                t.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Error connecting to the server: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
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

    /**
     * Go back to previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}