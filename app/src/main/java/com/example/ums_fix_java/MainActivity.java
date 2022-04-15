package com.example.ums_fix_java;

import static Scraper.mainVars.scraper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import Scraper.ScrapeWebsite;

public class MainActivity extends AppCompatActivity {

    String loginUname;
    String loginPass;
    Button button;
    boolean mode;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mode = false;
        sharedPreferences = getSharedPreferences("userLogin" ,MODE_PRIVATE);
//        sharedPreferences.edit().remove("username").apply();
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

        if(mode){
            try {
                startAppRemember();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        button.setOnClickListener(view -> {
            try {
                if(!mode)startAppNoRemember();
                else;
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Wrong login information", Toast.LENGTH_LONG);
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

                Scraper.mainVars.scraper = new ScrapeWebsite(loginUname,
                        loginPass,
                        rememberMe.isChecked());

                if (rememberMe.isChecked())
                    sharedPreferences.edit().putString("username", loginUname)
                            .putString("password", loginPass).apply();

                if (scraper.status) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void startAppRemember() throws IOException{
            Scraper.mainVars.scraper = new ScrapeWebsite(loginUname,
                    loginPass,
                    false);

            if (scraper.status) {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            }
        }

    }
