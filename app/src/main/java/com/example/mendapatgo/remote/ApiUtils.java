package com.example.mendapatgo.remote;

import com.example.mendapatgo.DatabaseHelper;

public class ApiUtils {

    // Base URL of REST API
    public static final String BASE_URL = "https://aptitude.my/hoteldb/api/";



    // Return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static RoomService getRoomService() {
        return RetrofitClient.getClient(BASE_URL).create(RoomService.class);
    }

    public static BookService getBookService() {
        return RetrofitClient.getClient(BASE_URL).create(BookService.class);
    }
}
