package com.example.mendapatgo.model;

public class Bookings {
    private int user_id;
    private int room_id;
    private String check_in_date;
    private String check_out_date;
    private int guests;
    private double total_price;

    public Bookings(int user_id, int room_id, String check_in_date, String check_out_date, int guests, double total_price) {
        this.user_id = user_id;
        this.room_id = room_id;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
        this.guests = guests;
        this.total_price = total_price;
    }
}