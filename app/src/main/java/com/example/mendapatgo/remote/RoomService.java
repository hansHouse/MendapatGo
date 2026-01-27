package com.example.mendapatgo.remote;

import com.example.mendapatgo.model.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RoomService {
    @GET("room?order=name&orderType=asc")
    Call<List<Room>> getAllRooms(@Header("api-key") String api_key);
}

