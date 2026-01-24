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

        edtUsername = findViewById(R.id.edtUsername); // input field for username or email
        edtPassword = findViewById(R.id.edtPassword);


    }

    /**
     * Login button click
     */
    public void loginClicked(View view) {
        Log.d("CLICK_TEST", "Button was clicked!");
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {
        Log.d("DEBUG_SEND", "Sending Key: username, Value: " + username);
        Log.d("DEBUG_SEND", "Sending Key: password, Value: " + password);

        UserService userService = ApiUtils.getUserService();

        Call<User> call = userService.login(username, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful() && response.body() != null) {

                    User user = response.body();

                    // Save session
                    SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                    spm.storeUser(user);

                    Toast.makeText(LoginActivity.this,
                            "Login successful", Toast.LENGTH_SHORT).show();

                    // Go to dashboard
                    Intent intent = new Intent(LoginActivity.this,
                            CustomerDashboardActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString()); // print error details to error log
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Server error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Client-side validation
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username or Email is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast
     */
    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
