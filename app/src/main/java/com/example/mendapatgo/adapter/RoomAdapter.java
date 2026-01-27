package com.example.mendapatgo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mendapatgo.R;
import com.example.mendapatgo.model.Room;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvRoomType;
        public TextView tvRoomPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);

            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<Room> roomsListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public RoomAdapter(Context context, List<Room> listData) {
        roomsListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.room_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Room m =roomsListData.get(position);
        holder.tvRoomType.setText(m.getRoom_type());
        holder.tvRoomPrice.setText((int) m.getPrice());
    }

    @Override
    public int getItemCount() {
        return roomsListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Room getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && roomsListData !=null && currentPos<roomsListData.size()) {
            return roomsListData.get(currentPos);
        }
        return null;
    }

}