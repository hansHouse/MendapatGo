package com.example.mendapatgo.remote;

import com.example.mendapatgo.model.BookingResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookService {

    /**
     * Create a new booking with payment
     * @param token - user authentication token
     * @param userId - ID of the user making the booking
     * @param roomId - ID of the room to book
     * @param checkInDate - check-in date (format: yyyy-MM-dd)
     * @param checkOutDate - check-out date (format: yyyy-MM-dd)
     * @param guests - number of guests
     * @param totalPrice - total price for the booking
     * @param bookingMethod - payment method (Credit Card, Debit Card, etc.)
     * @param paymentStatus - payment status (paid, pending, etc.)
     * @return response body
     */
    @FormUrlEncoded
    @POST("bookings")
    Call<ResponseBody> createBookingWithPayment(
            @Header("token") String token,
            @Field("user_id") int userId,
            @Field("room_id") int roomId,
            @Field("check_in_date") String checkInDate,
            @Field("check_out_date") String checkOutDate,
            @Field("guests") int guests,
            @Field("total_price") double totalPrice,
            @Field("booking_method") String bookingMethod,
            @Field("payment_status") String paymentStatus
    );

    /**
     * Get all bookings for a specific user
     * @param token - user authentication token
     * @param userId - ID of the user
     * @return list of bookings
     */
    @GET("bookings/user/{user_id}")
    Call<List<BookingResponse>> getUserBookings(
            @Header("token") String token,
            @Path("user_id") int userId
    );

    /**
     * Get a specific booking by ID
     * @param token - user authentication token
     * @param bookingId - ID of the booking
     * @return booking details
     */
    @GET("bookings/{booking_id}")
    Call<BookingResponse> getBookingById(
            @Header("token") String token,
            @Path("booking_id") int bookingId
    );

    /**
     * Cancel a booking
     * @param token - user authentication token
     * @param bookingId - ID of the booking to cancel
     * @return response body
     */
    @FormUrlEncoded
    @PUT("bookings/{booking_id}/cancel")
    Call<ResponseBody> cancelBooking(
            @Header("token") String token,
            @Path("booking_id") int bookingId
    );

    /**
     * Update booking status
     * @param token - user authentication token
     * @param bookingId - ID of the booking
     * @param status - new status
     * @return response body
     */
    @FormUrlEncoded
    @PUT("bookings/{booking_id}/status")
    Call<ResponseBody> updateBookingStatus(
            @Header("token") String token,
            @Path("booking_id") int bookingId,
            @Field("booking_status") String status
    );
}