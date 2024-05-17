package com.example.assignment3_smd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PasswordDB {
    private static final String DATABASE_NAME = "passwords.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PASSWORDS = "passwords";
    public static final String TABLE_RECYCLE_BIN = "recycle_bin";

    // Common column names
    public static final String COLUMN_ID = "id";

    // Users table column names
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Passwords table column names
    public static final String COLUMN_USER_ID = "userid";
    public static final String COLUMN_WEBSITE_NAME = "website_name";
    public static final String COLUMN_PASSWORD_USERNAME = "username";
    public static final String COLUMN_PASSWORD_PASSWORD = "password";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " +
            TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_USER_PASSWORD + " TEXT)";

    private static final String CREATE_TABLE_PASSWORDS = "CREATE TABLE " +
            TABLE_PASSWORDS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USER_ID + " INTEGER," +
            COLUMN_WEBSITE_NAME + " TEXT," +
            COLUMN_PASSWORD_USERNAME + " TEXT," +
            COLUMN_PASSWORD_PASSWORD + " TEXT," +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
            TABLE_USERS + "(" + COLUMN_ID + "))";

    private static final String CREATE_TABLE_RECYCLE_BIN = "CREATE TABLE " +
            TABLE_RECYCLE_BIN + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USER_ID + " INTEGER," +
            COLUMN_WEBSITE_NAME + " TEXT," +
            COLUMN_PASSWORD_USERNAME + " TEXT," +
            COLUMN_PASSWORD_PASSWORD + " TEXT," +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
            TABLE_USERS + "(" + COLUMN_ID + "))";

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public PasswordDB(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return database.insert(TABLE_USERS, null, values);
    }

    public long verifyUserAndGetId(String username, String password) {
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        long userId = -1;
        try (Cursor cursor = database.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            }
        }
        return userId;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long addPassword(long userId, String websiteName, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_WEBSITE_NAME, websiteName);
        values.put(COLUMN_PASSWORD_USERNAME, username);
        values.put(COLUMN_PASSWORD_PASSWORD, password);
        return database.insert(TABLE_PASSWORDS, null, values);
    }

    public ArrayList<String> getAllPasswords() {
        ArrayList<String> passwords = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PASSWORDS,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int websiteNameIndex = cursor.getColumnIndexOrThrow(COLUMN_WEBSITE_NAME);
            int usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_USERNAME);
            int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_PASSWORD);

            do {
                String websiteName = cursor.getString(websiteNameIndex);
                String username = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);

                String passwordDetails = "Website: " + websiteName + "\nUsername: " + username + "\nPassword: " + password;
                passwords.add(passwordDetails);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return passwords;
    }


    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_PASSWORDS);
            db.execSQL(CREATE_TABLE_RECYCLE_BIN);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECYCLE_BIN);
            onCreate(db);
        }
    }
}