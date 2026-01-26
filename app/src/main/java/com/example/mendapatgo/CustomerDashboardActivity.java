package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private Button btnLogout, btnBook, btnMyBookings;
    private RoomService roomService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvWelcome = findViewById(R.id.tvWelcome);
        rvRooms = findViewById(R.id.rvRooms);
        btnLogout = findViewById(R.id.btnLogout);
        btnBook = findViewById(R.id.btnBook);
        btnMyBookings = findViewById(R.id.btnMyBookings);

        // Standard RecyclerView Setup
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        if (user != null) {
            tvWelcome.setText("Welcome, " + user.getUsername());
            updateRecyclerView(user.getToken());
        } else {
            // If no user exists at all, then redirect
            clearSessionAndRedirect();
        }

        btnLogout.setOnClickListener(v -> clearSessionAndRedirect());
        btnBook.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
    }

    private void updateRecyclerView(String token) {
        // DEBUG: Check this in your Logcat!
        Log.d("MendapatGo", "Attempting with Token: " + token);

        roomService = ApiUtils.getRoomService();

        // Try passing the token. If your server needs "Bearer ", add it here.
        roomService.getAllRooms(token).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200 && response.body() != null) {
                    adapter = new RoomAdapter(CustomerDashboardActivity.this, response.body());
                    rvRooms.setAdapter(adapter);
                    rvRooms.addItemDecoration(new DividerItemDecoration(CustomerDashboardActivity.this, DividerItemDecoration.VERTICAL));
                } else if (response.code() == 401) {
                    // SERVER SAYS TOKEN IS WRONG
                    Toast.makeText(CustomerDashboardActivity.this, "Session Expired (401)", Toast.LENGTH_SHORT).show();
                    // Comment the next line out if you want to stay on screen to debug
                    // clearSessionAndRedirect();
                } else {
                    Toast.makeText(CustomerDashboardActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("MendapatGo", "Connection failed: " + t.getMessage());
            }
        });
    }

    public void clearSessionAndRedirect() {
        new SharedPrefManager(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}