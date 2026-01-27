package com.example.mendapatgo.model;

public class Room {
    private int room_id;
    private String room_number;
    private String room_type;
    private double price;
    private String status;

    public Room() {}

    // Getters
    public int getRoom_id() { return room_id; }
    public String getRoom_number() { return room_number; }
    public String getRoom_type() { return room_type; }
    public double getPrice() { return price; }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    @Override
    public String toString() {
        return "Room " + room_number + " (" + room_type + ") - RM" + price;
    }

}