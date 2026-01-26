package com.example.mendapatgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mendapatgo.model.FailLogin;
import com.example.mendapatgo.model.User;
import com.example.mendapatgo.remote.ApiUtils;
import com.example.mendapatgo.remote.UserService;
import com.example.mendapatgo.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    /**
     * Login button click
     */
    public void loginClicked(View view) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    /**
     * Call REST API to login and route based on Role
     */
    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call = userService.login(username, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // 1. Save session to Shared Preferences
                    SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                    spm.storeUser(user);

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // 2. Logic to switch between Admin and Customer
                    Intent intent;

                    // Check user role (Assuming your User model has getRole())
                    // Adjust "admin" to match exactly what your database/API returns
                    if (user.getRole() != null && user.getRole().equalsIgnoreCase("admin")) {
                        intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
                    }

                    startActivity(intent);
                    finish(); // Close login activity so user can't "back" into it

                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Server error: " + t.getMessage());
            }
        });
    }

    /**
     * Handles API error responses
     */
    private void handleError(Response<User> response) {
        try {
            String errorResp = response.errorBody().string();
            FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
            displayToast(e.getError().getMessage());
        } catch (Exception e) {
            Log.e("MyApp:", e.toString());
            displayToast("Login failed: Invalid credentials");
        }
    }

    private boolean validateLogin(String username, String password) {
        if (username.isEmpty()) {
            displayToast("Username or Email is required");
            return false;
        }
        if (password.isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}