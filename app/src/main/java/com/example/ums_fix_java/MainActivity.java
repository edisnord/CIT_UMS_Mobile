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

    // Function listens for different responses from the DrawerMenu
    ActivityResultLauncher<Intent> ActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onActivityResult(ActivityResult result) {
                    //If we get the code 10(Logout) from the DrawerActivity, we will keep the app
                    //open, reset the scraper instance and empty the user grade data
                    if(result.getResultCode() != 10)
                    finishAffinity();
                    else {
                        try {
                            ScrapeWebsite.resetScraper();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Reset the button click listener to only run the startAppNoRemember function
                        button.setOnClickListener(view -> {
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

        //Loading animation
        this.loginForm = findViewById(R.id.loginForm);
        loginForm.setVisibility(View.INVISIBLE);
        this.progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            ScrapeWebsite.getScraper();
            runOnUiThread(()->{
                //if saved user data was found, start startAppRemember() function,

                if(mode){
                    try {
                        startAppRemember();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //End animation if there is no saved data
                    progressBar.setVisibility(View.INVISIBLE);
                    loginForm.setVisibility(View.VISIBLE);
                }
            });
        }).start();

        //Login button, calls the login function that logs in without
        //saved data
        button.setOnClickListener(view -> {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    void startAppNoRemember() throws IOException {

        EditText username = findViewById(R.id.editTextTextPersonName);
        EditText password = findViewById(R.id.editTextTextPassword);
        CheckBox rememberMe = findViewById(R.id.checkBox);

        //If there is no saved user data, we set these variables to the user input
        loginUname = username.getText().toString();
        loginPass = password.getText().toString();


            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                //start animation
                loginForm.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        //Scrape all necessary data
                        ScrapeWebsite.getScraper().login(loginUname, loginPass);
                        ScrapeWebsite.getScraper().scrapeGrades();
                        ScrapeWebsite.getScraper().scrapeSubjects();
                        runOnUiThread(()->{
                            //When done start app
                            Intent intent = new Intent(this, DrawerMenu.class);
                            ActivityLauncher.launch(intent);
                        });
                    } catch (IOException e) {
                        runOnUiThread(()->{
                            //If scraping fails due to IOException, tell user that their login info is wrong
                            progressBar.setVisibility(View.INVISIBLE);
                            loginForm.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Wrong login information",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        });
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e){
                        runOnUiThread(()->{
                            //If scraping fails due to ArrayOutOfBoundsException,
                            // tell user that there has been a server error
                            progressBar.setVisibility(View.INVISIBLE);
                            loginForm.setVisibility(View.VISIBLE);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "There was an error with the server response, please restart" +
                                            "the application",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        });
                    }
                }).start();

                //If the user selected remember me, save their data in the shared preferences
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
                    //Scrape all necessary data
                    ScrapeWebsite.getScraper().login(loginUname, loginPass);
                    ScrapeWebsite.getScraper().scrapeGrades();
                    ScrapeWebsite.getScraper().scrapeSubjects();
                    runOnUiThread(()->{
                        //Start app when done
                        Intent intent = new Intent(this, DrawerMenu.class);
                        ActivityLauncher.launch(intent);
                    });
                } catch (IOException e) {
                    runOnUiThread(()->{
                        //If scraping fails due to IOException at this point it's a server error
                        progressBar.setVisibility(View.INVISIBLE);
                        loginForm.setVisibility(View.VISIBLE);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "There was an error with the server response, please restart" +
                                        "the application",
                                Toast.LENGTH_LONG);
                        toast.show();
                    });
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e){
                runOnUiThread(()->{
                    //If scraping fails due to ArrayOutOfBoundsException,
                    //tell user that there has been a server error
                    progressBar.setVisibility(View.INVISIBLE);
                    loginForm.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "There was an error with the server response, please restart" +
                                    "the application",
                            Toast.LENGTH_LONG);
                    toast.show();
                });
            }
            }).start();

        }

    }
