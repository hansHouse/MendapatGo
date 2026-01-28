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

import com.example.mendapatgo.adapter.AdminBookingAdapter;
import com.example.mendapatgo.model.Booking;
import com.example.mendapatgo.model.DeleteResponse;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookingService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageBookingsActivity extends AppCompatActivity {

    private BookingService bookingService;
    private RecyclerView rvBookingList;
    private AdminBookingAdapter adapter;

    // API key for accessing the booking service
    private static final String API_KEY = "83417780-aac0-43c8-8367-89821b949be1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_bookings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView
        rvBookingList = findViewById(R.id.rvBookingList);
        // register for context menu
        registerForContextMenu(rvBookingList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // fetch and update booking list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get booking service instance
        bookingService = ApiUtils.getBookingService();

        // execute the call
        bookingService.getAllBookings(API_KEY).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of booking objects from response
                    List<Booking> bookings = response.body();

                    // initialize adapter
                    adapter = new AdminBookingAdapter(getApplicationContext(), bookings);

                    // set adapter to the RecyclerView
                    rvBookingList.setAdapter(adapter);

                    // set layout to recycler view
                    rvBookingList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between items in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                            rvBookingList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvBookingList.addItemDecoration(dividerItemDecoration);
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Delete booking record. Called by contextual menu "Delete"
     * @param selectedBooking - booking selected by admin
     */
    private void doDeleteBooking(Booking selectedBooking) {
        // prepare REST API call
        BookingService bookingService = ApiUtils.getBookingService();
        Call<DeleteResponse> call = bookingService.deleteBooking(API_KEY, selectedBooking.getBookingId());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Booking successfully deleted");
                    // update data in list view
                    updateRecyclerView();
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
        inflater.inflate(R.menu.booking_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Booking selectedBooking = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedBooking.toString());

        if (item.getItemId() == R.id.menu_delete) {
            // user clicked the delete contextual menu
            doDeleteBooking(selectedBooking);
        } else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateBooking(selectedBooking);
        }
        return super.onContextItemSelected(item);
    }

    private void doUpdateBooking(Booking selectedBooking) {
        Log.d("MyApp:", "updating booking: " + selectedBooking.toString());
        // forward admin to UpdateBookingActivity, passing the selected booking id
        Intent intent = new Intent(getApplicationContext(), UpdateBookingActivity.class);
        intent.putExtra("booking_id", selectedBooking.getBookingId());
        startActivity(intent);
    }
}