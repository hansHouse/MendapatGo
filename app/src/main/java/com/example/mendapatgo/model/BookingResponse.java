package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BookingResponse implements Serializable {
    @SerializedName("booking_id")
    private int booking_id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("room_id")
    private int room_id;

    @SerializedName("room_number")
    private String room_number;

    @SerializedName("room_type")
    private String room_type;

    @SerializedName("check_in_date")
    private String check_in_date;

    @SerializedName("check_out_date")
    private String check_out_date;

    @SerializedName("guests")
    private int guests;

    @SerializedName("total_price")
    private double total_price;

    @SerializedName("booking_status")
    private String booking_status;

    @SerializedName("booking_method")
    private String booking_method;

    @SerializedName("payment_status")
    private String payment_status;

    @SerializedName("paid_at")
    private String paid_at;

    public BookingResponse() {}

    // Getters
    public int getBooking_id() {
        return booking_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public String getRoom_number() {
        return room_number;
    }

    public String getRoom_type() {
        return room_type;
    }

    public String getCheck_in_date() {
        return check_in_date;
    }

    public String getCheck_out_date() {
        return check_out_date;
    }

    public int getGuests() {
        return guests;
    }

    public double getTotal_price() {
        return total_price;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public String getBooking_method() {
        return booking_method;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getPaid_at() {
        return paid_at;
    }

    // Setters
    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public void setCheck_in_date(String check_in_date) {
        this.check_in_date = check_in_date;
    }

    public void setCheck_out_date(String check_out_date) {
        this.check_out_date = check_out_date;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public void setBooking_method(String booking_method) {
        this.booking_method = booking_method;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public void setPaid_at(String paid_at) {
        this.paid_at = paid_at;
    }
}