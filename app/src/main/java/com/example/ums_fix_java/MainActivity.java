package com.example.ums_fix_java;

import static Scraper.mainVars.scraper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import Scraper.ScrapeWebsite;

public class MainActivity extends AppCompatActivity {

    Button button;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        button = findViewById(R.id.press);
        button.setOnClickListener(view -> {
            try {
                startApp();
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Wrong login information", Toast.LENGTH_LONG);
                toast.show();
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void startApp() throws IOException {

        EditText username = findViewById(R.id.editTextTextPersonName);
        EditText password = findViewById(R.id.editTextTextPassword);

        if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            Scraper.mainVars.scraper = new ScrapeWebsite(username.getText().toString(), password.getText().toString());
            if(scraper.status) {
                Intent intent = new Intent(this, GradesActivity.class);
                startActivity(intent);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_LONG);
            toast.show();
        }

    }

}