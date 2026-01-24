package com.example.mendapatgo.remote;

import com.example.mendapatgo.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {


    @FormUrlEncoded
    @POST("users")
    Call<User> addUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("role") String role);



    // UserService.java
    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username, // Change this from "loginEmail"
            @Field("password") String password
    );


}
