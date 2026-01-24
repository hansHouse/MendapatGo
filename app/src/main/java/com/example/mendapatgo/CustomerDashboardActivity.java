package com.example.mendapatgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.sharedpref.SharedPrefManager;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ListView lvRooms;
    private Button btnLogout, btnMyBookings, btnBook;

    private int userId;
    private List<Room> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        userId = prefs.getInt("userId", -1);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvRooms = findViewById(R.id.lvRooms);
        btnLogout = findViewById(R.id.btnLogout);
        btnMyBookings = findViewById(R.id.btnMyBookings);

        tvWelcome.setText("Welcome, " + username + "!");



        lvRooms.setOnItemClickListener((parent, view, position, id) -> {
            Room room = roomList.get(position);

            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("roomId", room.getId());
            intent.putExtra("roomNumber", room.getRoom_number());
            intent.putExtra("roomType", room.getRoom_type());
            intent.putExtra("price", room.getPrice());
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnMyBookings.setOnClickListener(v ->
                startActivity(new Intent(this, MyBookingsActivity.class))
        );

        btnLogout.setOnClickListener(v -> logout());

        // greet the user
        // if the user is not logged in we will directly them to LoginActivity
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {    // no session record
            // stop this MainActivity
            finish();
            // forward to Login Page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            // Greet user
            User user = spm.getUser();
            tvWelcome.setText("Hello " + user.getUsername());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void logout() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit().clear().apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
