package com.example.assignment3_smd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Addpassword extends AppCompatActivity {
    EditText websiteNameEditText, usernameEditText, passwordEditText;
    PasswordDB db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        db = new PasswordDB(this);

        websiteNameEditText = findViewById(R.id.websiteNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPassword();
            }
        });
    }

    private void addPassword() {
        String websiteName = websiteNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (websiteName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            long userid = sharedPreferences.getLong("user_id", -1);
            Toast.makeText(this, "User ID: " + userid, Toast.LENGTH_SHORT).show();

            if (userid != -1) {
                db.open();
                long insertResult = db.addPassword(userid, websiteName, username, password);
                notifyAll();
                db.close();

                if (insertResult != -1) {
                    Toast.makeText(this, "Password added successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity and go back to the previous screen
                } else {
                    Toast.makeText(this, "Failed to add password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve user ID from SharedPreferences.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}