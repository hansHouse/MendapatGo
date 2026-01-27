package com.example.mendapatgo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.BookingActivity;
import com.example.mendapatgo.R;
import com.example.mendapatgo.model.Room;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> roomListData;
    private Context mContext;
    private int currentPos;

    public RoomAdapter(Context context, List<Room> listData) {
        this.roomListData = listData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Corrected to use your custom layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_customer_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomListData.get(position);
        holder.tvRoomType.setText(room.getRoom_type());
        holder.tvRoomPrice.setText("RM " + room.getPrice() + " per night");

        // Click Listener to go to Booking Page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BookingActivity.class);

            // Passing data to the next activity
            intent.putExtra("ROOM_ID", room.getRoom_id());
            intent.putExtra("ROOM_TYPE", room.getRoom_type());
            intent.putExtra("ROOM_PRICE", String.valueOf(room.getPrice()));

            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomListData != null ? roomListData.size() : 0;
    }

    // Single, clean ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvRoomType;
        public TextView tvRoomPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            // Replace R.id.xxxx with the actual IDs inside activity_customer_dashboard.xml
            //tvRoomType = itemView.findViewById(R.id.tvRoomType);
            //tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    // Helper method to get the position for long-press actions (like context menus)
    public int getCurrentPos() {
        return currentPos;
    }
}