package com.example.mendapatgo.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mendapatgo.model.User;

public class SharedPrefManager {

    // The constants
    private static final String SHARED_PREF_NAME = "hotelsharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_TOKEN = "keytoken";  // ✅ ADD TOKEN KEY

    private final Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    /**
     * Method to let the user login
     * This method will store the user data in shared preferences
     * @param user
     */
    public void storeUser(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, user.getToken());  // ✅ STORE TOKEN
        editor.apply();
    }

    /**
     * This method will check whether user is already logged in or not.
     * Return True if already logged in
     */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    /**
     * This method will give the information of logged in user, retrieved from SharedPreferences
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setRole(sharedPreferences.getString(KEY_ROLE, null));
        user.setToken(sharedPreferences.getString(KEY_TOKEN, null));  // ✅ RETRIEVE TOKEN

        return user;
    }

    /**
     * This method will logout the user. Clear the SharedPreferences
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}