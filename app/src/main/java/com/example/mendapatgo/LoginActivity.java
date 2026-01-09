package com.example.mendapatgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.loginUser(email, password);

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String username = cursor.getString(1);
            String role = cursor.getString(5);

            // Save user session
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", userId);
            editor.putString("username", username);
            editor.putString("role", role);
            editor.apply();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Redirect based on role
            Intent intent;
            if (role.equals("admin")) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}