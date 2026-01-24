package com.example.mendapatgo.model;

public class Room {

    private int room_id;
    private String room_number;
    private String room_type;
    private double price;
    private String status;
    private Integer updated_by; // nullable

    // Empty constructor (required for Retrofit / Gson)
    public Room() {
    }

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

    public Integer getUpdated_by() {
        return updated_by;
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

    public void setUpdated_by(Integer updated_by) {
        this.updated_by = updated_by;
    }


    public boolean getId() {
        return false;
    }
}
