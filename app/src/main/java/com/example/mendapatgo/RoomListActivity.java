package com.example.mendapatgo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mendapatgo.adapter.RoomAdapter;
import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.RoomService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomListActivity extends AppCompatActivity {

    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private RoomService roomService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        // Set Toolbar Title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select a Room");
        }

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        fetchRoomsFromDb();
    }

    private void fetchRoomsFromDb() {
        String token = SharedPrefManager.getInstance(this).getUser().getToken();
        roomService = ApiUtils.getRoomService();

        // Standardizing the Bearer token for your 401 error fix
        roomService.getAllRooms("Bearer " + token).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new RoomAdapter(RoomListActivity.this, response.body());
                    rvRooms.setAdapter(adapter);
                } else {
                    Toast.makeText(RoomListActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(RoomListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}