package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    // Constructor
    public DeleteResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}