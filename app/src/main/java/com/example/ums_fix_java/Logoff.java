package com.example.ums_fix_java;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

public class Logoff extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logoff, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        Button logoff = view.findViewById(R.id.button);

        //Remove username and password from shared preferences, go back to login screen with code 10
        logoff.setOnClickListener(view1 -> {
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("userLogin", MODE_PRIVATE);
            sharedPreferences.edit().remove("username").apply();
            sharedPreferences.edit().remove("password").apply();
            requireActivity().setResult(10);
            requireActivity().finish();
        });

    }

}