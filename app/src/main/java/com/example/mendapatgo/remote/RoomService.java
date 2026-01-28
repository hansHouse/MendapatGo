package com.example.mendapatgo.remote;

import com.example.mendapatgo.model.Room;
import com.example.mendapatgo.model.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoomService {

    /**
     * Get all rooms
     */
    @GET("rooms")
    Call<List<Room>> getAllRooms(@Header("api-key") String apiKey);

    /**
     * Get a single room by ID
     */
    @GET("rooms/{id}")
    Call<Room> getRoom(@Header("api-key") String apiKey, @Path("id") int roomId);

    /**
     * Add a new room
     */
    @FormUrlEncoded
    @POST("rooms")
    Call<Room> addRoom(
            @Header("api-key") String apiKey,
            @Field("room_number") String roomNumber,
            @Field("room_type") String roomType,
            @Field("price") double price,
            @Field("status") String status,
            @Field("updated_by") String updatedBy
    );

    /**
     * Update an existing room
     * CRITICAL: Uses POST with id in path, not PUT
     * This API expects form data, not JSON
     */
    @FormUrlEncoded
    @POST("rooms/{id}")
    Call<Room> updateRoom(
            @Header("api-key") String apiKey,
            @Path("id") int id,
            @Field("room_number") String room_number,
            @Field("room_type") String room_type,
            @Field("price") double price,
            @Field("status") String status
    );

    /**
     * Delete a room
     */
    @DELETE("rooms/{id}")
    Call<DeleteResponse> deleteRoom(
            @Header("api-key") String apiKey,
            @Path("id") int roomId
    );
}