package com.example.mendapatgo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MendapatGo.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_PHONE = "phone";
    private static final String COL_ROLE = "role";

    // Rooms table
    private static final String TABLE_ROOMS = "rooms";
    private static final String COL_ROOM_ID = "room_id";
    private static final String COL_ROOM_NUMBER = "room_number";
    private static final String COL_ROOM_TYPE = "room_type";
    private static final String COL_PRICE = "price";
    private static final String COL_STATUS = "status";

    // Bookings table
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COL_BOOKING_ID = "booking_id";
    private static final String COL_BOOKING_USER_ID = "user_id";
    private static final String COL_BOOKING_ROOM_ID = "room_id";
    private static final String COL_CHECK_IN = "check_in_date";
    private static final String COL_CHECK_OUT = "check_out_date";
    private static final String COL_GUESTS = "guests";
    private static final String COL_TOTAL_PRICE = "total_price";
    private static final String COL_BOOKING_STATUS = "booking_status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "(" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USERNAME + " TEXT," +
                COL_EMAIL + " TEXT UNIQUE," +
                COL_PASSWORD + " TEXT," +
                COL_PHONE + " TEXT," +
                COL_ROLE + " TEXT)";
        db.execSQL(createUsersTable);

        // Create rooms table
        String createRoomsTable = "CREATE TABLE " + TABLE_ROOMS + "(" +
                COL_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ROOM_NUMBER + " TEXT," +
                COL_ROOM_TYPE + " TEXT," +
                COL_PRICE + " REAL," +
                COL_STATUS + " TEXT)";
        db.execSQL(createRoomsTable);

        // Create bookings table
        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + "(" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_BOOKING_USER_ID + " INTEGER," +
                COL_BOOKING_ROOM_ID + " INTEGER," +
                COL_CHECK_IN + " TEXT," +
                COL_CHECK_OUT + " TEXT," +
                COL_GUESTS + " INTEGER," +
                COL_TOTAL_PRICE + " REAL," +
                COL_BOOKING_STATUS + " TEXT," +
                "FOREIGN KEY(" + COL_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")," +
                "FOREIGN KEY(" + COL_BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COL_ROOM_ID + "))";
        db.execSQL(createBookingsTable);

        // Insert default admin
        ContentValues admin = new ContentValues();
        admin.put(COL_USERNAME, "admin");
        admin.put(COL_EMAIL, "admin@mendapatgo.com");
        admin.put(COL_PASSWORD, "admin123");
        admin.put(COL_PHONE, "1234567890");
        admin.put(COL_ROLE, "admin");
        db.insert(TABLE_USERS, null, admin);

        // Insert sample rooms
        insertSampleRoom(db, "101", "Single", 500000, "available");
        insertSampleRoom(db, "102", "Double", 750000, "available");
        insertSampleRoom(db, "103", "Suite", 1200000, "available");
    }

    private void insertSampleRoom(SQLiteDatabase db, String number, String type, double price, String status) {
        ContentValues room = new ContentValues();
        room.put(COL_ROOM_NUMBER, number);
        room.put(COL_ROOM_TYPE, type);
        room.put(COL_PRICE, price);
        room.put(COL_STATUS, status);
        db.insert(TABLE_ROOMS, null, room);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Register user
    public boolean registerUser(String username, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_PHONE, phone);
        values.put(COL_ROLE, "customer");

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Login user
    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password});
    }

    // Get all available rooms
    public Cursor getAvailableRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS +
                " WHERE " + COL_STATUS + "='available'", null);
    }

    // Get all rooms (for admin)
    public Cursor getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS, null);
    }

    // Create booking
    public boolean createBooking(int userId, int roomId, String checkIn, String checkOut,
                                 int guests, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_USER_ID, userId);
        values.put(COL_BOOKING_ROOM_ID, roomId);
        values.put(COL_CHECK_IN, checkIn);
        values.put(COL_CHECK_OUT, checkOut);
        values.put(COL_GUESTS, guests);
        values.put(COL_TOTAL_PRICE, totalPrice);
        values.put(COL_BOOKING_STATUS, "pending");

        long result = db.insert(TABLE_BOOKINGS, null, values);

        if (result != -1) {
            // Update room status
            ContentValues roomValues = new ContentValues();
            roomValues.put(COL_STATUS, "booked");
            db.update(TABLE_ROOMS, roomValues, COL_ROOM_ID + "=?",
                    new String[]{String.valueOf(roomId)});
            return true;
        }
        return false;
    }

    // Get user bookings
    public Cursor getUserBookings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT b.*, r." + COL_ROOM_NUMBER + ", r." + COL_ROOM_TYPE +
                        " FROM " + TABLE_BOOKINGS + " b " +
                        "INNER JOIN " + TABLE_ROOMS + " r ON b." + COL_BOOKING_ROOM_ID + "=r." + COL_ROOM_ID +
                        " WHERE b." + COL_BOOKING_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    // Get all bookings (for admin)
    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT b.*, u." + COL_USERNAME + ", r." + COL_ROOM_NUMBER +
                " FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_USERS + " u ON b." + COL_BOOKING_USER_ID + "=u." + COL_USER_ID +
                " INNER JOIN " + TABLE_ROOMS + " r ON b." + COL_BOOKING_ROOM_ID + "=r." + COL_ROOM_ID, null);
    }

    // Add room (admin)
    public boolean addRoom(String roomNumber, String roomType, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROOM_NUMBER, roomNumber);
        values.put(COL_ROOM_TYPE, roomType);
        values.put(COL_PRICE, price);
        values.put(COL_STATUS, "available");

        long result = db.insert(TABLE_ROOMS, null, values);
        return result != -1;
    }

    // Update booking status
    public boolean updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, status);

        int result = db.update(TABLE_BOOKINGS, values, COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(bookingId)});
        return result > 0;
    }
}