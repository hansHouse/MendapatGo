package com.example.mendapatgo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.BookingDetailsActivity;
import com.example.mendapatgo.R;
import com.example.mendapatgo.model.BookingResponse;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<BookingResponse> bookingList;
    private Context context;

    public BookingAdapter(Context context, List<BookingResponse> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingResponse booking = bookingList.get(position);

        // Set booking details
        holder.tvBookingId.setText("Booking #" + booking.getBooking_id());
        holder.tvRoomInfo.setText("Room " + booking.getRoom_number() + " - " + booking.getRoom_type());
        holder.tvCheckInDate.setText("Check-in: " + booking.getCheck_in_date());
        holder.tvCheckOutDate.setText("Check-out: " + booking.getCheck_out_date());
        holder.tvTotalPrice.setText(String.format("RM %.2f", booking.getTotal_price()));

        // Set booking status
        String status = booking.getBooking_status() != null ? booking.getBooking_status() : "Pending";
        holder.tvStatus.setText(status);

        // Set status color
        int statusColor;
        switch (status.toLowerCase()) {
            case "confirmed":
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "pending":
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "cancelled":
                statusColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "completed":
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                break;
            default:
                statusColor = context.getResources().getColor(android.R.color.darker_gray);
        }
        holder.tvStatus.setTextColor(statusColor);

        // Click listener to view booking details
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra("BOOKING", booking);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvBookingId, tvRoomInfo, tvCheckInDate, tvCheckOutDate, tvTotalPrice, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvCheckInDate = itemView.findViewById(R.id.tvCheckInDate);
            tvCheckOutDate = itemView.findViewById(R.id.tvCheckOutDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}