package com.example.mendapatgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import com.example.mendapatgo.adapter.AdminRoomAdapter;
import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.model.DeleteResponse;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageRoomsActivity extends AppCompatActivity {

    private RoomService roomService;
    private RecyclerView rvRoomList;
    private AdminRoomAdapter adapter;

    // API key for accessing the room service
    private static final String API_KEY = "83417780-aac0-43c8-8367-89821b949be1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_rooms);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView
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
        // get room service instance
        roomService = ApiUtils.getRoomService();

        Log.d("MyApp:", "=== Fetching Rooms ===");
        Log.d("MyApp:", "API URL: " + ApiUtils.BASE_URL + "rooms");
        Log.d("MyApp:", "API Key: " + API_KEY);

        // execute the call
        roomService.getAllRooms(API_KEY).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                Log.d("MyApp:", "=== Response Received ===");
                Log.d("MyApp:", "Response Code: " + response.code());
                Log.d("MyApp:", "Response Message: " + response.message());
                Log.d("MyApp:", "Response Headers: " + response.headers().toString());

                if (response.code() == 200) {
                    // Get list of room objects from response
                    List<Room> rooms = response.body();

                    Log.d("MyApp:", "✅ SUCCESS: Retrieved " + (rooms != null ? rooms.size() : 0) + " rooms");

                    // initialize adapter
                    adapter = new AdminRoomAdapter(getApplicationContext(), rooms);

                    // set adapter to the RecyclerView
                    rvRoomList.setAdapter(adapter);

                    // set layout to recycler view
                    rvRoomList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between items in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                            rvRoomList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvRoomList.addItemDecoration(dividerItemDecoration);

                } else if (response.code() == 401) {
                    // Unauthorized error
                    Log.e("MyApp:", "❌ ERROR 401: Unauthorized");
                    Log.e("MyApp:", "This means the API key is invalid, expired, or not properly sent");

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("MyApp:", "Error Body: " + errorBody);
                            Toast.makeText(getApplicationContext(),
                                    "Unauthorized: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Unauthorized: Invalid or missing API key",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("MyApp:", "Error reading error body: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Unauthorized: Check API key configuration",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.e("MyApp:", "❌ ERROR: " + response.code() + " - " + response.message());

                    // Try to get more error details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("MyApp:", "Error Body: " + errorBody);
                            Toast.makeText(getApplicationContext(),
                                    "Error " + response.code() + ": " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("MyApp:", "Error reading error body: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Error: " + response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("MyApp:", "=== API CALL FAILED ===");
                Log.e("MyApp:", "Error Type: " + t.getClass().getName());
                Log.e("MyApp:", "Error Message: " + t.getMessage());
                t.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Error connecting to server: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Delete room record. Called by contextual menu "Delete"
     * @param selectedRoom - room selected by admin
     */
    private void doDeleteRoom(Room selectedRoom) {
        // prepare REST API call
        RoomService roomService = ApiUtils.getRoomService();
        Call<DeleteResponse> call = roomService.deleteRoom(API_KEY, selectedRoom.getRoomId());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Room successfully deleted");
                    // update data in list view
                    updateRecyclerView();
                } else if (response.code() == 401) {
                    Log.e("MyApp:", "❌ ERROR 401: Unauthorized on delete");
                    displayAlert("Unauthorized: Cannot delete room. Check API key.");
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Room selectedRoom = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedRoom.toString());

        if (item.getItemId() == R.id.menu_delete) {
            // user clicked the delete contextual menu
            doDeleteRoom(selectedRoom);
        } else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateRoom(selectedRoom);
        }
        return super.onContextItemSelected(item);
    }

    private void doUpdateRoom(Room selectedRoom) {
        Log.d("MyApp:", "updating room: " + selectedRoom.toString());
        // forward admin to UpdateRoomActivity, passing the selected room id
        Intent intent = new Intent(getApplicationContext(), UpdateRoomActivity.class);
        intent.putExtra("room_id", selectedRoom.getRoomId());
        startActivity(intent);
    }

    /**
     * Action handler for Add Room floating action button
     * @param view
     */
    public void floatingAddRoomClicked(View view) {
        // forward admin to NewRoomActivity
        Intent intent = new Intent(getApplicationContext(), NewRoomActivity.class);
        startActivity(intent);
    }
}