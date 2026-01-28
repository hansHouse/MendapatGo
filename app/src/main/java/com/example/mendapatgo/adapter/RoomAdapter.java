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
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomListData.get(position);

        // Display room information
        holder.tvRoomNumber.setText("Room: " + room.getRoom_number());
        holder.tvRoomType.setText("Type: " + room.getRoom_type());
        holder.tvRoomPrice.setText(String.format("RM %.2f per night", room.getPrice()));

        // Display status if available
        if (room.getStatus() != null && !room.getStatus().isEmpty()) {
            holder.tvRoomStatus.setText("Status: " + room.getStatus());
            holder.tvRoomStatus.setVisibility(View.VISIBLE);

            // Set status color based on availability
            switch (room.getStatus().toLowerCase()) {
                case "available":
                    holder.tvRoomStatus.setTextColor(
                            mContext.getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "occupied":
                    holder.tvRoomStatus.setTextColor(
                            mContext.getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case "maintenance":
                    holder.tvRoomStatus.setTextColor(
                            mContext.getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                default:
                    holder.tvRoomStatus.setTextColor(
                            mContext.getResources().getColor(android.R.color.darker_gray));
            }
        } else {
            holder.tvRoomStatus.setVisibility(View.GONE);
        }

        // ✅ Click Listener to open BookingActivity
        holder.itemView.setOnClickListener(v -> {
            // Only allow booking if room is available
            if (room.getStatus() != null && room.getStatus().equalsIgnoreCase("available")) {
                Intent intent = new Intent(mContext, BookingActivity.class);

                // Pass room details to booking activity
                intent.putExtra("ROOM_ID", room.getRoom_id());
                intent.putExtra("ROOM_NUMBER", room.getRoom_number());
                intent.putExtra("ROOM_TYPE", room.getRoom_type());
                intent.putExtra("ROOM_PRICE", room.getPrice());

                // Add FLAG_ACTIVITY_NEW_TASK since we're starting from adapter context
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);
            } else {
                // Show message if room is not available
                android.widget.Toast.makeText(mContext,
                        "This room is not available for booking",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomListData != null ? roomListData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvRoomNumber;
        public TextView tvRoomType;
        public TextView tvRoomPrice;
        public TextView tvRoomStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomStatus = itemView.findViewById(R.id.tvRoomStatus);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Room getSelectedRoom() {
        if (currentPos >= 0 && currentPos < roomListData.size()) {
            return roomListData.get(currentPos);
        }
        return null;
    }
}