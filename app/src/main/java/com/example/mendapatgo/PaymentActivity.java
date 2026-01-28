package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.BookingService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";

    private TextView tvBookingSummary, tvPaymentAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCreditCard, rbDebitCard, rbOnlineBanking, rbEWallet;
    private Button btnPayNow;

    // Booking details passed from BookingActivity
    private int roomId, userId, guests;
    private String roomNumber, roomType, checkInDate, checkOutDate;
    private double totalPrice;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Log.d(TAG, "=== PaymentActivity Started ===");

        // Initialize views
        tvBookingSummary = findViewById(R.id.tvBookingSummary);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbDebitCard = findViewById(R.id.rbDebitCard);
        rbOnlineBanking = findViewById(R.id.rbOnlineBanking);
        rbEWallet = findViewById(R.id.rbEWallet);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Get booking details from intent
        getBookingDetails();

        // Display booking summary
        displayBookingSummary();

        // Set up payment button
        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void getBookingDetails() {
        Intent intent = getIntent();
        roomId = intent.getIntExtra("ROOM_ID", 0);
        roomNumber = intent.getStringExtra("ROOM_NUMBER");
        roomType = intent.getStringExtra("ROOM_TYPE");
        checkInDate = intent.getStringExtra("CHECK_IN_DATE");
        checkOutDate = intent.getStringExtra("CHECK_OUT_DATE");
        guests = intent.getIntExtra("GUESTS", 1);
        totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0);

        // Get user ID and token from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        userId = user.getId();
        token = user.getToken();

        // ✅ DEBUG LOGGING
        Log.d(TAG, "=== Booking Details ===");
        Log.d(TAG, "Room ID: " + roomId);
        Log.d(TAG, "Room Number: " + roomNumber);
        Log.d(TAG, "Room Type: " + roomType);
        Log.d(TAG, "Check-in: " + checkInDate);
        Log.d(TAG, "Check-out: " + checkOutDate);
        Log.d(TAG, "Guests: " + guests);
        Log.d(TAG, "Total Price: " + totalPrice);
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Token: " + (token != null ? "EXISTS (length=" + token.length() + ")" : "NULL ❌"));

        // Validate critical data
        if (userId <= 0) {
            Log.e(TAG, "❌ ERROR: Invalid User ID!");
            Toast.makeText(this, "Error: User session invalid. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "❌ ERROR: Token is NULL or EMPTY!");
            Toast.makeText(this, "Error: Authentication token missing. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (roomId <= 0) {
            Log.e(TAG, "❌ ERROR: Invalid Room ID!");
            Toast.makeText(this, "Error: Invalid room selection.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void displayBookingSummary() {
        String summary = "Room: " + roomNumber + " - " + roomType + "\n" +
                "Check-in: " + checkInDate + "\n" +
                "Check-out: " + checkOutDate + "\n" +
                "Guests: " + guests;

        tvBookingSummary.setText(summary);
        tvPaymentAmount.setText(String.format("RM %.2f", totalPrice));
    }

    private void processPayment() {
        // Validate payment method selection
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected payment method
        RadioButton selectedPaymentMethod = findViewById(selectedPaymentId);
        String paymentMethod = selectedPaymentMethod.getText().toString();

        Log.d(TAG, "Payment method selected: " + paymentMethod);

        // Show confirmation dialog
        showPaymentConfirmation(paymentMethod);
    }

    private void showPaymentConfirmation(String paymentMethod) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Payment");
        builder.setMessage(
                "Payment Method: " + paymentMethod + "\n" +
                        "Amount: RM " + String.format("%.2f", totalPrice) + "\n\n" +
                        "Proceed with payment?"
        );

        builder.setPositiveButton("Pay Now", (dialog, which) -> {
            submitBookingWithPayment(paymentMethod);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void submitBookingWithPayment(String paymentMethod) {
        // Show loading
        btnPayNow.setEnabled(false);
        btnPayNow.setText("Processing...");

        Log.d(TAG, "=== SUBMITTING BOOKING TO API ===");
        Log.d(TAG, "API URL: " + ApiUtils.BASE_URL + "bookings");
        Log.d(TAG, "Token: " + (token != null ? "Present" : "NULL"));
        Log.d(TAG, "User ID (user_id): " + userId);
        Log.d(TAG, "Room ID (room_id): " + roomId);
        Log.d(TAG, "Check-in Date: " + checkInDate);
        Log.d(TAG, "Check-out Date: " + checkOutDate);
        Log.d(TAG, "Guests: " + guests);
        Log.d(TAG, "Total Price: " + totalPrice);
        Log.d(TAG, "Payment Method (booking_method): " + paymentMethod);
        Log.d(TAG, "Payment Status: paid");

        // ✅ FIXED: Changed BookService to BookingService and getBookService to getBookingService
        BookingService bookingService = ApiUtils.getBookingService();
        Call<ResponseBody> call = bookingService.createBookingWithPayment(
                token,
                userId,
                roomId,
                checkInDate,
                checkOutDate,
                guests,
                totalPrice,
                paymentMethod,
                "paid"  // payment status
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnPayNow.setEnabled(true);
                btnPayNow.setText("Pay Now");

                Log.d(TAG, "=== API RESPONSE ===");
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Message: " + response.message());
                Log.d(TAG, "Response Headers: " + response.headers().toString());

                if (response.code() == 200 || response.code() == 201) {
                    Log.d(TAG, "✅ SUCCESS: Booking created successfully!");

                    // Try to log response body
                    try {
                        if (response.body() != null) {
                            String responseBody = response.body().string();
                            Log.d(TAG, "Response Body: " + responseBody);
                        } else {
                            Log.d(TAG, "Response Body: NULL (but request was successful)");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading response body: " + e.getMessage());
                    }

                    showPaymentSuccessDialog();

                } else if (response.code() == 401) {
                    Log.e(TAG, "❌ ERROR 401: Unauthorized - Token invalid or expired");

                    // Try to read error body for more details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }

                    Toast.makeText(PaymentActivity.this,
                            "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    finish();

                } else if (response.code() == 400) {
                    Log.e(TAG, "❌ ERROR 400: Bad Request - Invalid data sent to API");

                    // Try to get error details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                            Toast.makeText(PaymentActivity.this,
                                    "Payment failed: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PaymentActivity.this,
                                    "Payment failed: Bad request. Please check your input.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                        Toast.makeText(PaymentActivity.this,
                                "Payment failed: Bad request",
                                Toast.LENGTH_LONG).show();
                    }

                } else if (response.code() == 500) {
                    Log.e(TAG, " ERROR 500: Internal Server Error");

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }

                    Toast.makeText(PaymentActivity.this,
                            "Server error. Please try again later.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Log.e(TAG, " ERROR " + response.code() + ": " + response.message());

                    // Try to get error details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                            Toast.makeText(PaymentActivity.this,
                                    "Payment failed: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PaymentActivity.this,
                                    "Payment failed: " + response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                        Toast.makeText(PaymentActivity.this,
                                "Payment failed: " + response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnPayNow.setEnabled(true);
                btnPayNow.setText("Pay Now");

                Log.e(TAG, "=== API CALL FAILED ===");
                Log.e(TAG, "Error Type: " + t.getClass().getName());
                Log.e(TAG, "Error Message: " + t.getMessage());
                t.printStackTrace();

                String errorMessage = "Connection error: ";

                // Provide more specific error messages
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage += "Cannot reach server. Check your internet connection.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage += "Request timed out. Server not responding.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage += "Cannot connect to server.";
                } else {
                    errorMessage += t.getMessage();
                }

                Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPaymentSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Successful!");
        builder.setMessage(
                "Your payment has been processed successfully.\n\n" +
                        "Booking confirmed for:\n" +
                        "Room: " + roomNumber + "\n" +
                        "Check-in: " + checkInDate + "\n" +
                        "Check-out: " + checkOutDate + "\n\n" +
                        "You can view your booking details in 'My Bookings'."
        );

        builder.setPositiveButton("View My Bookings", (dialog, which) -> {
            // Go to My Bookings activity
            Intent intent = new Intent(PaymentActivity.this, MyBookingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Back to Dashboard", (dialog, which) -> {
            // Go back to dashboard
            Intent intent = new Intent(PaymentActivity.this, CustomerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        // Confirm if user wants to cancel payment
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Payment?");
        builder.setMessage("Are you sure you want to cancel this payment?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            super.onBackPressed();
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}