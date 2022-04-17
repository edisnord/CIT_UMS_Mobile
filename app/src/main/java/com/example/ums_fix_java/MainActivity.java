package com.example.ums_fix_java;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Scraper.ScrapeWebsite;
import Scraper.ScrapeWebsite;

public class MainActivity extends AppCompatActivity {

    String loginUname;
    String loginPass;
    Button button;
    boolean mode;
    SharedPreferences sharedPreferences;
    LinearLayout loginForm;
    ProgressBar progressBar;

    ActivityResultLauncher<Intent> ActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() != 10)
                    finishAffinity();
                    else {
                        try {
                            ScrapeWebsite.resetScraper();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        button.setOnClickListener(view -> {
                            ScrapeWebsite.gradeRows = new ArrayList<>();
                            ScrapeWebsite.subjectRows = new ArrayList<>();
                            try {
                                startAppNoRemember();
                            } catch (IOException e) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Wrong login information",
                                        Toast.LENGTH_LONG);
                                toast.show();
                                e.printStackTrace();
                            }
                        });
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    loginForm.setVisibility(View.VISIBLE);
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Fetch session token

        mode = false;
        //Check private storage
        sharedPreferences = getSharedPreferences("userLogin" ,MODE_PRIVATE);
        //Check for user's remembered login info
        if(!(sharedPreferences.getString("username","").equals("")
                || sharedPreferences.getString("password", "").equals(""))){
            loginUname = sharedPreferences.getString("username","");
            loginPass = sharedPreferences.getString("password", "");
            mode = true;
        }


        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        button = findViewById(R.id.press);

        this.loginForm = findViewById(R.id.loginForm);
        loginForm.setVisibility(View.INVISIBLE);
        this.progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                ScrapeWebsite.getScraper();
                runOnUiThread(()->{
                    if(mode){
                        try {
                            startAppRemember();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginForm.setVisibility(View.VISIBLE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        button.setOnClickListener(view -> {
            try {
                if(!mode)startAppNoRemember();
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Wrong login information",
                        Toast.LENGTH_LONG);
                toast.show();
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void startAppNoRemember() throws IOException {

        EditText username = findViewById(R.id.editTextTextPersonName);
        EditText password = findViewById(R.id.editTextTextPassword);
        CheckBox rememberMe = findViewById(R.id.checkBox);

        loginUname = username.getText().toString();
        loginPass = password.getText().toString();


            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                loginForm.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        ScrapeWebsite.getScraper().login(loginUname, loginPass, rememberMe.isChecked());
                        ScrapeWebsite.getScraper().scrapeGrades();
                        ScrapeWebsite.getScraper().scrapeSubjects();
                        runOnUiThread(()->{
                            Intent intent = new Intent(this, DrawerMenu.class);
                            ActivityLauncher.launch(intent);
                        });
                    } catch (IOException e) {
                        runOnUiThread(()->{
                            progressBar.setVisibility(View.INVISIBLE);
                            loginForm.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Wrong login information",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        });
                        e.printStackTrace();
                    }
                }).start();

                if (rememberMe.isChecked())
                    sharedPreferences.edit().putString("username", loginUname)
                            .putString("password", loginPass).apply();

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void startAppRemember() throws IOException{
            new Thread(() -> {
                try {
                    ScrapeWebsite.getScraper().login(loginUname, loginPass, false);
                    ScrapeWebsite.getScraper().scrapeGrades();
                    ScrapeWebsite.getScraper().scrapeSubjects();
                    runOnUiThread(()->{
                        Intent intent = new Intent(this, DrawerMenu.class);
                        ActivityLauncher.launch(intent);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }
