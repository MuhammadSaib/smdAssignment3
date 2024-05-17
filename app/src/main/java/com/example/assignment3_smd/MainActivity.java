package com.example.assignment3_smd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;
    PasswordDB db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new PasswordDB(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if the user ID exists in SharedPreferences
        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId != -1) {
            Intent intent = new Intent(MainActivity.this, ListofPasswords.class);
            startActivity(intent);
            finish(); // Finish the MainActivity to prevent going back
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonSignup = findViewById(R.id.buttonSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                loginUser();
                db.close();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                signupUser();
                db.close();
            }
        });
    }

    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        long userId = db.verifyUserAndGetId(username, password); // Method to verify user and get ID
        if (userId != -1) {
            saveUserId(userId);
            Intent intent = new Intent(MainActivity.this, ListofPasswords.class);
            startActivity(intent);
            finish(); // Finish the MainActivity to prevent going back
        } else {
            // Login failed, show error message
            Toast.makeText(MainActivity.this, "Wrong credentials. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (db.isUsernameExists(username)) {
            Toast.makeText(MainActivity.this, "Username already exists. Please choose a different one.", Toast.LENGTH_SHORT).show();
        } else {
            long userId = db.addUser(username, password);
            if (userId != -1) {
                // Signup successful, save user ID and start ListofPasswords activity
                saveUserId(userId);
                Intent intent = new Intent(MainActivity.this, ListofPasswords.class);
                startActivity(intent);
                finish(); // Finish the MainActivity to prevent going back
            } else {
                // Signup failed, show error message
                Toast.makeText(MainActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserId(long userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("user_id", userId);
        editor.apply();
    }
}