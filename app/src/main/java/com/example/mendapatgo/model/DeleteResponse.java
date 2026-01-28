package com.example.mendapatgo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for delete operations
 * The API returns a complex structure, not just a simple boolean
 */
public class DeleteResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DeleteData data;

    // Inner class for the nested "data" object
    public static class DeleteData {
        @SerializedName("success")
        private boolean success;

        @SerializedName("message")
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public DeleteData getData() {
        return data;
    }

    public boolean isSuccess() {
        // Check if status is 200 OR if data.success is true
        return status == 200 || (data != null && data.isSuccess());
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.isSuccess() : "null") +
                '}';
    }
}