package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("room_id")
    private int room_id;

    @SerializedName("room_number")
    private String room_number;

    @SerializedName("room_type")
    private String room_type;

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status;

    public Room() {}

    // Getters
    public int getRoom_id() {
        return room_id;
    }

    public String getRoom_number() {
        return room_number;
    }

    public String getRoom_type() {
        return room_type;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room " + room_number + " (" + room_type + ") - RM" + price;
    }
}