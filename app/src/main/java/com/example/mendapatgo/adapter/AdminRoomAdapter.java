package com.example.mendapatgo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.R;
import com.example.mendapatgo.model.Room;

import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvRoomNumber;
        public TextView tvRoomType;
        public TextView tvPrice;
        public TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Room> roomListData;
    private Context mContext;
    private int currentPos;

    public AdminRoomAdapter(Context context, List<Room> listData) {
        roomListData = listData;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Changed to use admin_room_list_item instead of room_list_item
        View view = inflater.inflate(R.layout.admin_room_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room room = roomListData.get(position);
        holder.tvRoomNumber.setText("Room " + room.getRoomNumber());
        holder.tvRoomType.setText(room.getRoomType());
        holder.tvPrice.setText(String.format("RM %.2f", room.getPrice()));
        holder.tvStatus.setText(room.getStatus());

        // Set color based on status
        switch (room.getStatus()) {
            case "Available":
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "Occupied":
                holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            case "Maintenance":
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
        }
    }

    @Override
    public int getItemCount() {
        return roomListData.size();
    }

    /**
     * Return room object for currently selected room
     * @return Room object
     */
    public Room getSelectedItem() {
        if (currentPos >= 0 && roomListData != null && currentPos < roomListData.size()) {
            return roomListData.get(currentPos);
        }
        return null;
    }
}