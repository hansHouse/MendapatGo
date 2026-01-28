package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("check_in_date")
    private String checkInDate;

    @SerializedName("check_out_date")
    private String checkOutDate;

    @SerializedName("guests")
    private int guests;

    @SerializedName("booking_status")
    private String bookingStatus;

    @SerializedName("booking_method")
    private String bookingMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("payed_at")
    private String payedAt;

    @SerializedName("created_at")
    private String createdAt;

    // Constructor
    public Booking(int bookingId, int userId, int roomId, String checkInDate, String checkOutDate, int guests) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guests = guests;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingMethod() {
        return bookingMethod;
    }

    public void setBookingMethod(String bookingMethod) {
        this.bookingMethod = bookingMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPayedAt() {
        return payedAt;
    }

    public void setPayedAt(String payedAt) {
        this.payedAt = payedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", guests=" + guests +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}