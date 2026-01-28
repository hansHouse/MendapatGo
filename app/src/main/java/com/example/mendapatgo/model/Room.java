package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("room_number")
    private String roomNumber;

    @SerializedName("room_type")
    private String roomType;

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status;

    @SerializedName("updated_by")
    private String updatedBy;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;

    // Constructor
    public Room(int roomId, String roomNumber, String roomType, double price, String status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.status = status;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    // Alternative getter for compatibility with RoomAdapter
    public int getRoom_id() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    // Alternative getter for compatibility with RoomAdapter
    public String getRoom_number() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    // Alternative getter for compatibility with RoomAdapter
    public String getRoom_type() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}