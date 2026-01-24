package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etPhone;
    private Button btnRegister;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize Views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);

        // Get API service instance
        userService = ApiUtils.getUserService();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    public void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = "user"; // Default role for pRESTige

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute Network Request
        Call<User> call = userService.addUser(username, email, password, phone, role);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success Case
                    Toast.makeText(getApplicationContext(),
                            "Account created for " + response.body().getUsername(),
                            Toast.LENGTH_LONG).show();

                    // Go to Login Activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 409) {
                    // Conflict Case (Email exists)
                    Toast.makeText(RegisterActivity.this,
                            "Error: Email or Username already exists.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Other Errors (401 Unauthorized, 500 Server Error, etc.)
                    Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message());
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed. Check API Auth settings.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Connection/Server failure
                Log.e("NETWORK_ERROR", t.getMessage());
                Toast.makeText(RegisterActivity.this,
                        "Server error: Connection refused",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}