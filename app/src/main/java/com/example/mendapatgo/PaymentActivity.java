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
import com.example.mendapatgo.remote.BookService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvBookingSummary, tvPaymentAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCreditCard, rbDebitCard, rbOnlineBanking, rbEWallet;
    private Button btnPayNow;

    // Booking details passed from BookingActivity
    private int roomId, userId, guests;
    private String roomNumber, roomType, checkInDate, checkOutDate;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

        // Get user ID from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        userId = user.getId();
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

        // Get user token
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        String token = user.getToken();

        // Create booking with payment
        BookService bookService = ApiUtils.getBookService();
        Call<ResponseBody> call = bookService.createBookingWithPayment(
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

                if (response.code() == 200 || response.code() == 201) {
                    showPaymentSuccessDialog();
                } else if (response.code() == 401) {
                    Toast.makeText(PaymentActivity.this,
                            "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this,
                            "Payment failed: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnPayNow.setEnabled(true);
                btnPayNow.setText("Pay Now");

                Toast.makeText(PaymentActivity.this,
                        "Connection error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("PaymentActivity", "Payment failed", t);
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