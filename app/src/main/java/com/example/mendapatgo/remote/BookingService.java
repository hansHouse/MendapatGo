package com.example.mendapatgo.remote;

import com.example.mendapatgo.model.Booking;
import com.example.mendapatgo.model.BookingResponse;
import com.example.mendapatgo.model.DeleteResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface BookingService {

    // Get all bookings (for admin)
    @GET("bookings")
    Call<List<Booking>> getAllBookings(@Header("api-key") String apiKey);

    // Get single booking by ID
    @GET("bookings/{bookingId}")
    Call<Booking> getBooking(
            @Header("api-key") String apiKey,
            @Path("bookingId") int bookingId
    );

    // Get user's bookings
    @GET("bookings/user/{userId}")
    Call<List<BookingResponse>> getUserBooking(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    // Delete a booking
    @DELETE("bookings/{bookingId}")
    Call<DeleteResponse> deleteBooking(
            @Header("api-key") String apiKey,
            @Path("bookingId") int bookingId
    );

    // Update booking status
    // Alternative if your API requires PUT
    @FormUrlEncoded
    @PUT("bookings/{bookingId}/status")
    Call<Booking> updateBookingStatus(
            @Header("api-key") String apiKey,
            @Path("bookingId") int bookingId,
            @Field("booking_status") String bookingStatus,
            @Field("payment_status") String paymentStatus
    );

    // Create booking with payment
    @FormUrlEncoded
    @POST("bookings")
    Call<ResponseBody> createBookingWithPayment(
            @Header("Authorization") String token,
            @Field("user_id") int userId,
            @Field("room_id") int roomId,
            @Field("check_in_date") String checkInDate,
            @Field("check_out_date") String checkOutDate,
            @Field("guests") int guests,
            @Field("total_price") double totalPrice,
            @Field("booking_method") String bookingMethod,
            @Field("payment_status") String paymentStatus
    );
}