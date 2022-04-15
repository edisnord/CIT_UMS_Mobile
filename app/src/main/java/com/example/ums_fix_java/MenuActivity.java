package com.example.ums_fix_java;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import Scraper.ScrapeWebsite;
import Scraper.ScrapeWebsite;

public class MenuActivity extends AppCompatActivity {

    Group buttons;
    ProgressBar progressBar;

    ActivityResultLauncher<Intent> ActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                        progressBar.setVisibility(View.INVISIBLE);
                        buttons.setVisibility(View.VISIBLE);
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button buttonGr = findViewById(R.id.gradesButton);
        Button buttonPr = findViewById(R.id.profileButton);
        Button logoff = findViewById(R.id.logout);

        buttons = findViewById(R.id.buttons);
        progressBar = findViewById(R.id.progressBar3);

        buttonGr.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.INVISIBLE);
            new Thread(() -> {
                try {
                    if (ScrapeWebsite.gradeRows.isEmpty()) ScrapeWebsite.getScraper().scrapeGrades();
                    if (ScrapeWebsite.subjectRows.isEmpty()) ScrapeWebsite.getScraper().scrapeSubjects();
                    runOnUiThread(() -> {
                        Intent intent1 = new Intent(this, GradesActivity.class);
                        ActivityLauncher.launch(intent1);
                    });
                } catch (IOException e) {
                    runOnUiThread(()->{
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Scraping error",
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                    );
                    e.printStackTrace();
                }
            }).start();

        });

        buttonPr.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.INVISIBLE);
            new Thread(() -> {
                try {
                    if (ScrapeWebsite.gradeRows.isEmpty()) ScrapeWebsite.getScraper().scrapeGrades();
                    if (ScrapeWebsite.subjectRows.isEmpty()) ScrapeWebsite.getScraper().scrapeSubjects();
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, ProfileActivity.class);
                        ActivityLauncher.launch(intent);
                    });
                } catch (IOException e) {
                    runOnUiThread(()->{
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Scraping error",
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                            );
                    e.printStackTrace();
                }
            }).start();

        });

        logoff.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("userLogin", MODE_PRIVATE);
            sharedPreferences.edit().remove("username").apply();
            sharedPreferences.edit().remove("password").apply();
            setResult(10);
            finish();
        });
    }



}