package com.example.ums_fix_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button buttonGr = findViewById(R.id.gradesButton);
        Button buttonPr = findViewById(R.id.profileButton);
        Button logoff = findViewById(R.id.logout);

        buttonGr.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, GradesActivity.class);
            startActivity(intent1);
        });

        buttonPr.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        logoff.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("userLogin" ,MODE_PRIVATE);
            sharedPreferences.edit().remove("username").apply();
            sharedPreferences.edit().remove("password").apply();
            finish();
        });

    }
}