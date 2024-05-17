package com.example.assignment3_smd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListofPasswords extends AppCompatActivity {

    ListView passwordListView;
    PasswordDB db;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_passwords);

        passwordListView = findViewById(R.id.passwordListView);
        db = new PasswordDB(this);

        ArrayList<String> passwords = db.getAllPasswords();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, passwords);
        passwordListView.setAdapter(adapter);

        passwordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPassword = (String) parent.getItemAtPosition(position);
                showDeleteConfirmationDialog(selectedPassword);
                return true;
            }
        });

        FloatingActionButton addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListofPasswords.this, Addpassword.class);
                startActivity(intent);
            }
        });
    }

    private void showDeleteConfirmationDialog(final String selectedPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this password?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePassword(selectedPassword);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deletePassword(String passwordDetails) {
        adapter.remove(passwordDetails);
        adapter.notifyDataSetChanged();
    }
}