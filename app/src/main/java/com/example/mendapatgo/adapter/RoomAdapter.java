package com.example.mendapatgo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mendapatgo.model.Room;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> roomListData;
    private Context mContext;

    public RoomAdapter(Context context, List<Room> listData) {
        this.roomListData = listData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using built-in simple layout
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomListData.get(position);
        holder.text1.setText(room.getRoom_type());
        holder.text2.setText("RM " + room.getPrice() + " per night");
    }

    @Override
    public int getItemCount() {
        return roomListData != null ? roomListData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text1, text2;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}