package com.example.mendapatgo.remote;

public class ApiUtils {

    // Base URL of REST API
    public static final String BASE_URL = "https://aptitude.my/hoteldb/api/";



    // Return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
