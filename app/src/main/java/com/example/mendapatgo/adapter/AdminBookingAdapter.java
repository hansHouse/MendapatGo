package com.example.mendapatgo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.R;
import com.example.mendapatgo.model.Booking;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvBookingId;
        public TextView tvRoomInfo;
        public TextView tvDates;
        public TextView tvBookingStatus;
        public TextView tvPaymentStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Booking> bookingListData;
    private Context mContext;
    private int currentPos;

    public AdminBookingAdapter(Context context, List<Booking> listData) {
        bookingListData = listData;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Changed to use admin_booking_list_item instead of booking_list_item
        View view = inflater.inflate(R.layout.admin_booking_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking booking = bookingListData.get(position);
        holder.tvBookingId.setText("Booking #" + booking.getBookingId());
        holder.tvRoomInfo.setText("Room " + booking.getRoomId()); // You may want to fetch room details
        holder.tvDates.setText(booking.getCheckInDate() + " to " + booking.getCheckOutDate());
        holder.tvBookingStatus.setText(booking.getBookingStatus());
        holder.tvPaymentStatus.setText(booking.getPaymentStatus());

        // Set color based on booking status
        switch (booking.getBookingStatus()) {
            case "Confirmed":
                holder.tvBookingStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "Pending":
                holder.tvBookingStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "Cancelled":
                holder.tvBookingStatus.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            case "Checked-in":
                holder.tvBookingStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
                break;
            case "Checked-out":
                holder.tvBookingStatus.setTextColor(Color.parseColor("#9E9E9E")); // Gray
                break;
        }

        // Set color based on payment status
        switch (booking.getPaymentStatus()) {
            case "Paid":
                holder.tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "Pending":
                holder.tvPaymentStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "Refunded":
                holder.tvPaymentStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookingListData.size();
    }

    /**
     * Return booking object for currently selected booking
     * @return Booking object
     */
    public Booking getSelectedItem() {
        if (currentPos >= 0 && bookingListData != null && currentPos < bookingListData.size()) {
            return bookingListData.get(currentPos);
        }
        return null;
    }
}