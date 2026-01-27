package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
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

    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private RoomService roomService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvRooms = findViewById(R.id.rvRooms);
        registerForContextMenu(rvRooms);

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        roomService = ApiUtils.getRoomService();
        roomService.getAllRooms(token).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                // Log the raw response for debugging
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    List<Room> rooms = response.body();
                    adapter = new RoomAdapter(getApplicationContext(), rooms);
                    rvRooms.setAdapter(adapter);
                    rvRooms.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRooms.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvRooms.addItemDecoration(dividerItemDecoration);
                }
                else if (response.code() == 401) {
                    // Only redirect if it's truly an authentication error
                    Toast.makeText(getApplicationContext(), "Session Expired. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", "Server Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network Error: Check your connection", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}